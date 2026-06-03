package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.data.Sort;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.model.SimpleCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    List<SimpleCategory> findAll(Sort sort);

    Optional<SimpleCategory> findBySlug(String slug);

    Optional<SimpleCategory> findById(ULID categoryId);

    List<SimpleCategory> findByProductId(ULID id);
}
