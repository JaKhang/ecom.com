package com.nlu.store.core.data;


import java.util.Objects;

/**
 * A concrete implementation of the {@link Pageable} interface.
 * Represents pagination information, including page number, page size, and sorting details.
 */
public class PageRequest implements Pageable {

    private final int page;
    private final int limit;
    private final Sort sort;

    /**
     * Creates a new PageRequest instance.
     *
     * @param page  the current page number (zero-based index)
     * @param limit the maximum number of items per page
     * @param sort  the sorting information
     */
    public PageRequest(int page, int limit, Sort sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be negative.");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero.");
        }
        this.page = page;
        this.limit = limit;
        this.sort = Objects.requireNonNullElse(sort, Sort.UNSORTED);
    }

    /**
     * Creates a new PageRequest instance with default sorting (unsorted).
     *
     * @param page  the current page number (zero-based index)
     * @param limit the maximum number of items per page
     */
    public PageRequest(int page, int limit) {
        this(page, limit, Sort.UNSORTED);
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public int getOffset() {
        return (page - 1) * limit;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    /**
     * Creates a new PageRequest instance for the next page.
     *
     * @return a new PageRequest for the next page
     */
    public PageRequest next() {
        return new PageRequest(page + 1, limit, sort);
    }

    /**
     * Creates a new PageRequest instance for the previous page.
     *
     * @return a new PageRequest for the previous page, or the current page if already at the first page
     */
    public PageRequest previous() {
        return page == 0 ? this : new PageRequest(page - 1, limit, sort);
    }

    /**
     * Creates a new PageRequest instance with updated sorting.
     *
     * @param sort the new sorting information
     * @return a new PageRequest with the updated sorting
     */
    public PageRequest withSort(Sort sort) {
        return new PageRequest(page, limit, sort);
    }

    @Override
    public String toString() {
        return String.format("PageRequest [page=%d, limit=%d, sort=%s]", page, limit, sort);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PageRequest that = (PageRequest) obj;
        return page == that.page && limit == that.limit && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, limit, sort);
    }
}
