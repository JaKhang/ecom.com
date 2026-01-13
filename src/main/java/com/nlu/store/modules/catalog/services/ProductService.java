package com.nlu.store.modules.catalog.services;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dto.ProductFilter;
import com.nlu.store.modules.catalog.model.SimpleProduct;
import com.nlu.store.modules.catalog.model.SimpleVariant;
import com.nlu.store.modules.catalog.model.details.*;

import java.util.*;

public interface ProductService {
    Page<SimpleProduct> findAll(Pageable pageable);

    Page<SimpleProduct> searchFullText(String keyword, ULID CategoryId, Pageable pageable);

    Page<SimpleProduct> searchAndFilter(String keyword, ProductFilter filter, Pageable pageable);

    List<ProductVariant> findVariantsByProductId(ULID slug);

    List<ProductAttribute> findAttributesByProductId(ULID slug);

    Optional<SimpleProduct> findBySlug(String slug);

    Optional<SimpleProduct> findById(ULID productId);

    List<ProductImage> findProductGallery(ULID productId);

    List<ProductSpecs> findProductSpecs(ULID productId);

    Optional<SimpleVariant> findDefaultVariantForCart(ULID productId);

    Optional<SimpleVariant> findSimpleVariant(ULID variantId);

    List<SimpleVariant> findSimpleVariantsByIds(Collection<ULID> variantIds);
    List<ProductVariant> findVariantsByIds(Collection<ULID> variantIds);

    Map<ULID, List<VariantValue>> findVariantValueByVariantIds(Set<ULID> variantIds);
}
