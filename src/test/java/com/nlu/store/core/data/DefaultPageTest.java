package com.nlu.store.core.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PageRequest} class using JUnit 5.
 */
class DefaultPageTest {

    @Test
    void givenValidParameters_whenCreatingPageRequest_thenPropertiesShouldBeSetCorrectly() {
        // Given
        int page = 1;
        int limit = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        // When
        PageRequest pageRequest = new PageRequest(page, limit, sort);

        // Then
        assertEquals(page, pageRequest.getPage(), "Page number should be set correctly.");
        assertEquals(limit, pageRequest.getLimit(), "Limit should be set correctly.");
        assertEquals(10, pageRequest.getOffset(), "Offset should be calculated correctly (page * limit).");
        assertEquals(sort, pageRequest.getSort(), "Sort object should be set correctly.");
    }

    @Test
    void givenNoSort_whenCreatingPageRequest_thenDefaultSortShouldBeUnsorted() {
        // Given
        int page = 0;
        int limit = 20;

        // When
        PageRequest pageRequest = new PageRequest(page, limit);

        // Then
        assertEquals(Sort.UNSORTED, pageRequest.getSort(), "Default sort should be UNSORTED.");
    }

    @Test
    void givenNegativePage_whenCreatingPageRequest_thenThrowsIllegalArgumentException() {
        // Given
        int page = -1;
        int limit = 10;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new PageRequest(page, limit);
        });
        assertEquals("Page index must not be negative.", exception.getMessage());
    }

    @Test
    void givenZeroOrNegativeLimit_whenCreatingPageRequest_thenThrowsIllegalArgumentException() {
        // Given
        int page = 0;
        int limit = 0;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new PageRequest(page, limit);
        });
        assertEquals("Limit must be greater than zero.", exception.getMessage());
    }

    @Test
    void givenPageRequest_whenNavigatingToNextPage_thenNextPageShouldBeReturned() {
        // Given
        int page = 1;
        int limit = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest pageRequest = new PageRequest(page, limit, sort);

        // When
        PageRequest nextPageRequest = pageRequest.next();

        // Then
        assertEquals(2, nextPageRequest.getPage(), "Next page should increment the page number.");
        assertEquals(limit, nextPageRequest.getLimit(), "Limit should remain the same for the next page.");
        assertEquals(sort, nextPageRequest.getSort(), "Sort should remain the same for the next page.");
    }

    @Test
    void givenFirstPageRequest_whenNavigatingToPreviousPage_thenSamePageShouldBeReturned() {
        // Given
        int page = 0;
        int limit = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest pageRequest = new PageRequest(page, limit, sort);

        // When
        PageRequest previousPageRequest = pageRequest.previous();

        // Then
        assertEquals(page, previousPageRequest.getPage(), "Previous page should not go below zero.");
        assertEquals(limit, previousPageRequest.getLimit(), "Limit should remain the same for the previous page.");
        assertEquals(sort, previousPageRequest.getSort(), "Sort should remain the same for the previous page.");
    }

    @Test
    void givenPageRequest_whenUpdatingSort_thenNewSortShouldBeApplied() {
        // Given
        int page = 1;
        int limit = 10;
        Sort initialSort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest pageRequest = new PageRequest(page, limit, initialSort);
        Sort newSort = Sort.by(Sort.Direction.DESC, "date");

        // When
        PageRequest updatedPageRequest = pageRequest.withSort(newSort);

        // Then
        assertEquals(page, updatedPageRequest.getPage(), "Page number should remain the same.");
        assertEquals(limit, updatedPageRequest.getLimit(), "Limit should remain the same.");
        assertEquals(newSort, updatedPageRequest.getSort(), "Sort should be updated correctly.");
    }


    @Test
    void givenTwoPageRequestsWithDifferentProperties_whenCheckingEquality_thenTheyShouldNotBeEqual() {
        // Given
        PageRequest pageRequest1 = new PageRequest(1, 10, Sort.by(Sort.Direction.ASC, "name"));
        PageRequest pageRequest2 = new PageRequest(1, 10, Sort.by(Sort.Direction.DESC, "name"));

        // When & Then
        assertNotEquals(pageRequest1, pageRequest2, "PageRequests with different properties should not be equal.");
    }
}
