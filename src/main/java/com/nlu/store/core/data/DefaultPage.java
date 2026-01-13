package com.nlu.store.core.data;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A default implementation of the {@link Page} interface.
 *
 * @param <T> the type of content in the page
 */
public class DefaultPage<T> implements Page<T> {

    private final List<T> content;      // The content of the current page
    private final int page;            // The current page number (zero-based index)
    private final int limit;           // The maximum number of elements per page
    private final int totalElements;   // The total number of elements across all pages
    private final int totalPages;      // The total number of pages

    private static final int START_PAGE = 0; // Zero-based index for the first page

    /**
     * Constructs a new {@code DefaultPage} instance.
     *
     * @param content       the content of the current page
     * @param page          the current page number (zero-based index)
     * @param limit         the maximum number of elements per page
     * @param totalElements the total number of elements across all pages
     */
    public DefaultPage(List<T> content, int page, int limit, int totalElements) {
        if (page < START_PAGE) {
            throw new IllegalArgumentException("Page index must not be negative.");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero.");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements must not be negative.");
        }

        this.content = content != null ? content : List.of(); // Default to an empty list if null
        this.page = page;
        this.limit = limit;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / limit); // Calculate total pages
    }

    @Override
    public <U> Page<U> map(Function<T, U> mapper) {
        List<U> mappedContent = content.stream().map(mapper).toList();
        return new DefaultPage<>(mappedContent, page, limit, totalElements);
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public boolean isFirst() {
        return page == START_PAGE;
    }

    @Override
    public boolean isLast() {
        return page == totalPages - 1;
    }

    @Override
    public int getNextPage() {
        return isLast() ? page : page + 1;
    }

    @Override
    public int getPreviousPage() {
        return isFirst() ? page : page - 1;
    }

    @Override
    public int totalElements() {
        return totalElements;
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean isLastPage() {
        return isLast();
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return content.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        content.forEach(action);
    }

    @Override
    public String toString() {
        return String.format(
                "DefaultPage{page=%d, limit=%d, totalElements=%d, totalPages=%d, contentSize=%d}",
                page, limit, totalElements, totalPages, content.size()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DefaultPage<?> that = (DefaultPage<?>) obj;
        return page == that.page &&
                limit == that.limit &&
                totalElements == that.totalElements &&
                totalPages == that.totalPages &&
                content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, page, limit, totalElements, totalPages);
    }
}
