package com.nlu.store.core.data;

/**
 * Interface representing pagination information.
 * Provides details about page size, offset, current page, and sorting.
 */
public interface Pageable {

    /**
     * Returns the maximum number of items per page.
     *
     * @return the limit (number of items per page)
     */
    int getLimit();

    /**
     * Returns the zero-based offset of the first item on the current page.
     *
     * @return the offset (starting position of the items)
     */
    int getOffset();

    /**
     * Returns the current page number (zero-based index).
     *
     * @return the current page
     */
    int getPage();

    /**
     * Returns the sorting information for the current page.
     *
     * @return the {@link Sort} object containing sorting details
     */
    Sort getSort();
}
