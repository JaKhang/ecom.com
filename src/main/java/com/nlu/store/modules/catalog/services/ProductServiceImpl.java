package com.nlu.store.modules.catalog.services;

import com.nlu.store.core.cache.Cache;
import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dao.ProductDAO;
import com.nlu.store.modules.catalog.dto.ProductFilter;
import com.nlu.store.modules.catalog.model.SimpleProduct;
import com.nlu.store.modules.catalog.model.SimpleVariant;
import com.nlu.store.modules.catalog.model.details.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;

@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;
    private final Cache cache;
    private final int maxCachedPage;
    private final Set<Integer> cacheableLimits;
    private static final String CACHE_PREFIX = "PRODUCTS_PAGE";
    private static final long CACHE_TTL_SECONDS = 300; // 5 phút

    @Inject
    public ProductServiceImpl(ProductDAO productDAO, Cache cache, PropertySource config) {
        this.productDAO = productDAO;
        this.cache = cache;
        this.maxCachedPage = config.getInt("catalog.cache.max_pages", 5);
        this.cacheableLimits = config.getIntegerSet("catalog.cache.allowed_limits", Set.of(12, 24, 48));

    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<SimpleProduct> findAll(Pageable pageable) {
        boolean isCacheable = shouldCache(pageable);
        String key = isCacheable ? buildCacheKey(pageable) : null;
        if (isCacheable) {
            Object cachedValue = cache.get(key);
            if (cachedValue instanceof Page) {
                return (Page<SimpleProduct>) cachedValue;
            }
        }
        Page<SimpleProduct> result = productDAO.findAll(pageable);

        if (isCacheable && result != null && !result.getContent().isEmpty()) {
            cache.put(key, result, CACHE_TTL_SECONDS);
        }

        return result;
    }

    @Override
    public Page<SimpleProduct> searchFullText(String keyword, ULID catId, Pageable pageable) {
        String cleanKeyword = (keyword != null) ? keyword.trim() : null;
        return productDAO.search(cleanKeyword, catId, pageable);
    }

    @Override
    public Page<SimpleProduct> searchAndFilter(String keyword, ProductFilter filter, Pageable pageable) {
        System.out.println(filter);
        String cleanKeyword = (keyword != null) ? keyword.trim() : null;
        return productDAO.search(cleanKeyword, filter, pageable);
    }

    @Override
    public List<ProductVariant> findVariantsByProductId(ULID slug) {
        return productDAO.findVariantsByProductId(slug);
    }

    @Override
    public List<ProductAttribute> findAttributesByProductId(ULID slug) {
        return productDAO.findAttributesByProductId(slug);
    }

    @Override
    public Optional<SimpleProduct> findBySlug(String slug) {
        return productDAO.findBySlug(slug);
    }

    @Override
    public Optional<SimpleProduct> findById(ULID productId) {
        return productDAO.findById(productId);
    }

    @Override
    public List<ProductImage> findProductGallery(ULID productId) {
        return productDAO.findProductImage(productId);
    }

    @Override
    public List<ProductSpecs> findProductSpecs(ULID productId) {
        return productDAO.findProductSpecs(productId);
    }

    @Override
    public Optional<SimpleVariant> findDefaultVariantForCart(ULID productId) {
        return productDAO.findDefaultVariantForCart(productId);
    }

    @Override
    public Optional<SimpleVariant> findSimpleVariant(ULID variantId) {
        return productDAO.findSimpleVariantById(variantId);

    }



    @Override
    public List<SimpleVariant> findSimpleVariantsByIds(Collection<ULID> variantIds) {
        return productDAO.findSimpleVariantsByIds(variantIds);
    }

    @Override
    public List<ProductVariant> findVariantsByIds(Collection<ULID> variantIds) {
        return productDAO.findVariantsByIds(variantIds);
    }

    @Override
    public Map<ULID, List<VariantValue>> findVariantValueByVariantIds(Set<ULID> variantIds) {
        return Map.of();
    }

    private boolean shouldCache(Pageable p) {
        if (!cacheableLimits.contains(p.getLimit())) {
            return false;
        }
        return p.getPage() <= maxCachedPage;
    }

    private String buildCacheKey(Pageable p) {
        return String.format("%s:%d:%d:%s",
                CACHE_PREFIX,
                p.getLimit(),
                p.getOffset(),
                p.getSort() != null ? p.getSort().toString() : "def"
        );
    }
}
