package com.nlu.store.modules.catalog.services;

import com.nlu.store.core.cache.Cache;
import com.nlu.store.core.data.Sort;
import com.nlu.store.modules.catalog.dao.BrandDAO;
import com.nlu.store.modules.catalog.model.Brand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BrandServiceImpl implements BrandService {

    private final BrandDAO brandDAO;
    private final Cache cache;

    // TTL: 1800s = 30 phút
    private static final long CACHE_TTL_SECONDS = 1800;
    private static final String CACHE_PREFIX = "BRAND_LIST";

    @Inject
    public BrandServiceImpl(BrandDAO brandDAO, Cache cache) {
        this.brandDAO = brandDAO;
        this.cache = cache;
    }

    @Override
    @SuppressWarnings("unchecked") // Bỏ qua cảnh báo ép kiểu List từ Cache
    public List<Brand> findAll(Sort sort) {

        String cacheKey = buildCacheKey(sort);
        Object cachedValue = cache.get(cacheKey);

        // 1. Kiểm tra Cache
        if (cachedValue instanceof List) {
            return (List<Brand>) cachedValue;
        }

        // 2. Nếu Cache Miss -> Gọi DB
        List<Brand> dbData = brandDAO.findAll(sort);

        // 3. Lưu vào Cache nếu có dữ liệu
        if (dbData != null && !dbData.isEmpty()) {
            cache.put(cacheKey, dbData, CACHE_TTL_SECONDS);
        }

        return dbData;
    }

    @Override
    public Optional<Brand> findBySlug(String slug) {
        // Thường chi tiết (Detail) không cache chung key với List
        // Gọi trực tiếp DAO hoặc implement cache riêng theo ID/Slug nếu cần
        return brandDAO.findBySlug(slug);
    }

    /**
     * Helper tạo key cache dựa trên điều kiện sắp xếp
     */
    private String buildCacheKey(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return CACHE_PREFIX + ":DEFAULT";
        }

        return CACHE_PREFIX + ":" + sort;
    }
}
