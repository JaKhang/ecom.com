package com.nlu.store.modules.catalog.services;

import com.nlu.store.core.data.Sort;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.model.SimpleCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<SimpleCategory> findAll(Sort sort);

    Optional<SimpleCategory> findBySlug(String slug);

    Optional<SimpleCategory> findById(ULID categoryId);
}
