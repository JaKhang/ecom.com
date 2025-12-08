package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dto.AttributeRequest;
import com.nlu.store.modules.catalog.model.Attribute;

import java.util.List;
import java.util.Optional;

public interface AttributeDAO {

    /**
     * Search attributes by keyword (e.g., matching name or code).
     */
    Page<Attribute> search(String keyword, Pageable pageable);

    /**
     * Retrieve all attributes with pagination.
     */
    Page<Attribute> findAll(Pageable pageable);

    /**
     * Find a specific attribute by its ULID.
     */
    Optional<Attribute> findById(ULID id);

    /**
     * Create a new attribute.
     * @return The ULID of the created attribute.
     */
    ULID create(AttributeRequest attributeRequest);

    /**
     * Update an existing attribute.
     */
    void update(ULID id, AttributeRequest attributeRequest);

    /**
     * Delete an attribute by its ULID.
     */
    void deleteById(ULID id);


}

