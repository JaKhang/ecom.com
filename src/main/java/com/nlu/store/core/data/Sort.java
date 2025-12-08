package com.nlu.store.core.data;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents sorting functionality for a collection of data.
 * Provides methods to define sorting orders and directions.
 */
public class Sort implements Iterable<Sort.Order> {

    private final List<Order> orders;

    public static final Direction DEFAULT_DIRECTION = Direction.ASC;
    public static final Sort UNSORTED = new Sort(Collections.emptyList());

    /**
     * Creates a Sort instance with the specified list of orders.
     *
     * @param orders the list of sorting orders
     */
    public Sort(List<Order> orders) {
        this.orders = orders != null ? new ArrayList<>(orders) : Collections.emptyList();
    }

    /**
     * Creates a Sort instance with the specified direction and properties.
     *
     * @param direction the sorting direction
     * @param properties the properties to sort by
     */
    public Sort(Direction direction, List<String> properties) {
        this.orders = properties.stream()
                .map(property -> new Order(property, direction))
                .collect(Collectors.toList());
    }

    /**
     * Checks if the Sort instance is unsorted (i.e., no orders defined).
     *
     * @return true if unsorted, false otherwise
     */
    public boolean isUnsorted() {
        return orders.isEmpty();
    }

    @Override
    public Iterator<Order> iterator() {
        return orders.iterator();
    }

    @Override
    public void forEach(Consumer<? super Order> action) {
        orders.forEach(action);
    }

    @Override
    public Spliterator<Order> spliterator() {
        return orders.spliterator();
    }

    /**
     * Represents a single sorting order for a property.
     */
    public static class Order {
        private final String property;
        private final Direction direction;

        public Order(String property, Direction direction) {
            if (StringUtils.isBlank(property)) {
                throw new IllegalArgumentException("Property name must not be null or empty.");
            }
            this.property = property;
            this.direction = Objects.requireNonNull(direction, "Direction must not be null.");
        }

        public String getProperty() {
            return property;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    /**
     * Creates a Sort instance for the specified properties with the default direction.
     *
     * @param properties the properties to sort by
     * @return a new Sort instance
     */
    public static Sort by(String... properties) {
        return properties.length == 0 ? UNSORTED : new Sort(DEFAULT_DIRECTION, Arrays.asList(properties));
    }

    /**
     * Creates a Sort instance for the specified list of orders.
     *
     * @param orders the list of sorting orders
     * @return a new Sort instance
     */
    public static Sort by(List<Order> orders) {
        return orders.isEmpty() ? UNSORTED : new Sort(orders);
    }

    /**
     * Creates a Sort instance for the specified orders.
     *
     * @param orders the sorting orders
     * @return a new Sort instance
     */
    public static Sort by(Order... orders) {
        return new Sort(Arrays.asList(orders));
    }

    /**
     * Creates a Sort instance for the specified direction and properties.
     *
     * @param direction the sorting direction
     * @param properties the properties to sort by
     * @return a new Sort instance
     */
    public static Sort by(Direction direction, String... properties) {
        return by(Arrays.stream(properties)
                .map(property -> new Order(property, direction))
                .collect(Collectors.toList()));
    }

    /**
     * Represents the direction of sorting (ascending or descending).
     */
    public enum Direction {
        ASC,
        DESC;

        /**
         * Checks if the direction is ascending.
         *
         * @return true if ascending, false otherwise
         */
        public boolean isAscending() {
            return this == ASC;
        }

        /**
         * Checks if the direction is descending.
         *
         * @return true if descending, false otherwise
         */
        public boolean isDescending() {
            return this == DESC;
        }

        /**
         * Converts a string to a Direction, ignoring case.
         *
         * @param value the string value
         * @return the corresponding Direction
         * @throws IllegalArgumentException if the value is invalid
         */
        public static Direction fromString(String value) {
            try {
                return valueOf(value.trim().toUpperCase(Locale.US));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        String.format("Invalid value '%s' for direction; must be 'asc' or 'desc' (case insensitive).", value),
                        e
                );
            }
        }

        /**
         * Converts a string to an Optional Direction, ignoring case.
         *
         * @param value the string value
         * @return an Optional containing the Direction, or empty if the value is null/empty
         */
        public static Optional<Direction> fromOptionalString(String value) {
            if (StringUtils.isBlank(value)) {
                return Optional.empty();
            }
            return Optional.of(fromString(value));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sort orders1 = (Sort) o;
        return Objects.equals(orders, orders1.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orders);
    }
}
