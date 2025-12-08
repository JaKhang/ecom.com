package com.nlu.store.core.data;

import java.util.List;
import java.util.function.Function;

import java.util.Collection;

/**
 * Represents a paginated collection of elements of type {@code T}.
 * Provides methods for iterating, mapping, and retrieving pagination details.
 *
 * @param <T> the type of elements in this page
 */
public interface Page<T> extends Iterable<T> {

    /**
     * Transforms the current page of elements into a new page of elements of type {@code U}
     * by applying the provided mapping function to each element.
     *
     * @param <U>    the type of elements in the resulting page
     * @param mapper a function to apply to each element in the current page
     * @return a new {@code Page<U>} containing the transformed elements
     */
    <U> Page<U> map(Function<T, U> mapper);

    /**
     * Retrieves the content of the current page as a collection.
     *
     * @return a {@code Collection<T>} containing the elements of the current page
     */
    Collection<T> getContent();

    /**
     * Retrieves the current page number (zero-based index).
     *
     * @return the current page number
     */
    int getPage();

    /**
     * Retrieves the maximum number of elements allowed per page.
     *
     * @return the page size limit
     */
    int getLimit();

    /**
     * Checks if the current page is the first page in the pagination sequence.
     *
     * @return {@code true} if the current page is the first page, {@code false} otherwise
     */
    boolean isFirst();

    /**
     * Checks if the current page is the last page in the pagination sequence.
     *
     * @return {@code true} if the current page is the last page, {@code false} otherwise
     */
    boolean isLast();

    /**
     * Retrieves the next page number.
     * If the current page is the last page, this method may return the same page number or a special value.
     *
     * @return the next page number, or a special value if no next page is available
     */
    int getNextPage();

    /**
     * Retrieves the previous page number.
     * If the current page is the first page, this method may return the same page number or a special value.
     *
     * @return the previous page number, or a special value if no previous page is available
     */
    int getPreviousPage();

    /**
     * Retrieves the total number of elements available across all pages.
     *
     * @return the total number of elements
     */
    int totalElements();

    /**
     * Retrieves the number of elements in the current page.
     * This value may be less than or equal to the page {@link #getLimit()}.
     *
     * @return the number of elements in the current page
     */
    int size();

    /**
     * Checks if the current page is the last page in the pagination sequence.
     * This is a default implementation that calculates the result based on the page number,
     * limit, and total count.
     *
     * @return {@code true} if the current page is the last page, {@code false} otherwise
     */
    default boolean isLastPage() {
        return (getPage() + 1) * getLimit() >= totalElements();
    }


    static <T> Page<T> of(List<T> content, Integer totalElements, Pageable pageable) {
        return new DefaultPage<>(
                content,
                pageable.getPage(),
                pageable.getLimit(),
                totalElements


        );
    }



    static <T> Page<T> of(List<T> content, Integer page, Integer totalElements, Integer limit) {
        return new DefaultPage<>(content, page, limit,totalElements );
    }

}


