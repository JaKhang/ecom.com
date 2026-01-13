package com.nlu.store.modules.catalog.services;

import com.nlu.store.core.cache.Cache;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dao.CategoryDAO;
import com.nlu.store.modules.catalog.model.SimpleCategory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;
    private final Cache cache;

    // Cấu hình thời gian sống của Cache (ví dụ: 30 phút)
    private static final long CACHE_TTL_SECONDS = 1800;
    private static final String CACHE_PREFIX = "CATEGORY_LIST";

    @Inject
    public CategoryServiceImpl(CategoryDAO categoryDAO, Cache cache) {
        this.categoryDAO = categoryDAO;
        this.cache = cache;
    }

    @Override
    @SuppressWarnings("unchecked") // Bỏ qua cảnh báo ép kiểu List
    public List<SimpleCategory> findAll(Sort sort) {

        String cacheKey = buildCacheKey(sort);

        Object cachedValue = cache.get(cacheKey);

        if (cachedValue instanceof List) {
            return (List<SimpleCategory>) cachedValue;
        }

        List<SimpleCategory> dbData = categoryDAO.findAll(sort);

        if (dbData != null && !dbData.isEmpty()) {
            cache.put(cacheKey, dbData, CACHE_TTL_SECONDS);
        }

        return dbData;
    }

    @Override
    public Optional<SimpleCategory> findBySlug(String slug) {
        return categoryDAO.findBySlug(slug);
    }

    @Override
    public Optional<SimpleCategory> findById(ULID categoryId) {
        // TODO (PC, 15/12/2025): To change the body of an implemented method
        return  categoryDAO.findById(categoryId);
    }

    private String buildCacheKey(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return CACHE_PREFIX + ":DEFAULT";
        }

        return CACHE_PREFIX + ":" + sort;
    }


    public void clearCategoryCache() {
        cache.remove(CACHE_PREFIX + ":DEFAULT");
    }
}
