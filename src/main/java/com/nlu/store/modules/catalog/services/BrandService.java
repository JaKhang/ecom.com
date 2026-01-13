package com.nlu.store.modules.catalog.services;

import com.nlu.store.core.data.Sort;
import com.nlu.store.modules.catalog.model.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    List<Brand> findAll(Sort sortOrder);

    Optional<Brand> findBySlug(String slug);
}
