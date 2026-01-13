package com.nlu.store.modules.catalog.model.cart;

import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.model.SimpleVariant;
import com.nlu.store.modules.catalog.model.Summary;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cart implements Iterable<CartItem> {
    public static final String KEY = "CART";
    private final Map<ULID, CartItem> items;

    public Cart() {
        this.items = new LinkedHashMap<>();
    }


    public Collection<CartItem> getItems() {
        return items.values();
    }

    @Override
    public Iterator<CartItem> iterator() {
        return items.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super CartItem> action) {
        items.values().forEach(action);
    }

    public void add(CartItem newItem) {
        CartItem existingItem = items.get(newItem.getVariantId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
        } else {
            items.put(newItem.getVariantId(), newItem);
        }
    }


    public void update(ULID variantId, int quantity) {
        if (quantity <= 0) {
            items.remove(variantId);
        } else if (items.containsKey(variantId)) {
            items.get(variantId).setQuantity(quantity);
        }
    }


    public void remove(ULID variantId) {
        items.remove(variantId);
    }


    public BigDecimal getTotalPrice() {
        return items.values().stream()
                .filter(CartItem::isInStock) // Only calculate available items
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSubTotal() {
        return items.values().stream()
                .filter(CartItem::isInStock) // Only calculate available items
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalQuantity() {
        return items.values().stream()
                .filter(CartItem::isInStock) // Only count available items
                .mapToInt(CartItem::getQuantity)
                .sum();
    }


    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean contains(ULID variantId) {
        return items.containsKey(variantId);
    }

    public Summary getSummary(){
        BigDecimal total = getTotalPrice();
        BigDecimal sub = getSubTotal();
        BigDecimal discount = sub.subtract(total);
        if (discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO;

        return Summary.builder()
                .quantity(getTotalQuantity())
                .total(total)
                .subtotal(sub)
                .discount(discount)
                .build();
    }

    public Set<ULID> getVariantIds(){
        return items.keySet();
    }

    /**
     * Synchronizes the cart with the latest data from the database.
     *
     * @param latestVariants List of the latest variant data fetched from DB.
     * @return true if there were changes to price, stock, or availability; false otherwise.
     */
    public boolean refresh(List<SimpleVariant> latestVariants) {
        boolean isChanged = false;

        Map<ULID, SimpleVariant> dbMap = latestVariants.stream()
                .collect(Collectors.toMap(SimpleVariant::getId, Function.identity()));

        for (Map.Entry<ULID, CartItem> entry : items.entrySet()) {
            CartItem item = entry.getValue();
            SimpleVariant freshData = dbMap.get(item.getVariantId());

            // CASE 1: Product no longer exists in DB
            if (freshData == null) {
//                // Option A: Remove it immediately
//                iterator.remove();
//                isChanged = true;
//                continue;

                // Option B: Mark as out of stock (if you want to show "Item unavailable" in UI)
                item.setInStock(false);
                isChanged = true;
                continue;
            }

            // CASE 2: Handle Stock Status
            if (freshData.getStocks() <= 0) {
                // Mark as out of stock instead of removing
                if (item.isInStock()) {
                    item.setInStock(false);
                    isChanged = true;
                }
            } else {
                // Item is back in stock
                if (!item.isInStock()) {
                    item.setInStock(true);
                    isChanged = true;
                }

                // Check if requested quantity exceeds available stock
                if (item.getQuantity() > freshData.getStocks()) {
                    item.setQuantity(freshData.getStocks());
                    isChanged = true;
                }
            }

            // CASE 3: Price updates (Only update if item is in stock to avoid confusion, or always update)
            if (compareBigDecimal(item.getUnitPrice(), freshData.getPrice()) != 0) {
                item.setUnitPrice(freshData.getPrice());
                isChanged = true;
            }

            if (compareBigDecimal(item.getUnitOriginalPrice(), freshData.getOriginalPrice()) != 0) {
                item.setUnitOriginalPrice(freshData.getOriginalPrice());
            }

            // CASE 4: Metadata updates
            if (!Objects.equals(item.getVariantName(), freshData.getName())) {
                item.setVariantName(freshData.getName());
            }
            if (!Objects.equals(item.getThumbnail(), freshData.getThumbnail())) {
                item.setThumbnail(freshData.getThumbnail());
            }

            item.setRatingAvg(freshData.getRatingAvg());
            item.setReviewsCount(freshData.getReviewsCount());
        }

        return isChanged;
    }

    private int compareBigDecimal(BigDecimal b1, BigDecimal b2) {
        if (b1 == null && b2 == null) return 0;
        if (b1 == null) return -1;
        if (b2 == null) return 1;
        return b1.compareTo(b2);
    }

    public int getQuantity(ULID vId) {
        CartItem item = items.get(vId);
        return item == null ? 0 : item.getQuantity();
    }
}

