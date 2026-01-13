package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dto.ProductFilter;
import com.nlu.store.modules.catalog.model.SimpleProduct;
import com.nlu.store.modules.catalog.model.SimpleVariant;
import com.nlu.store.modules.catalog.model.details.ProductAttribute;
import com.nlu.store.modules.catalog.model.details.ProductImage;
import com.nlu.store.modules.catalog.model.details.ProductSpecs;
import com.nlu.store.modules.catalog.model.details.ProductVariant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Page<SimpleProduct> search(String keyword, ULID catsId, Pageable pageable);

    Page<SimpleProduct> search(String keyword, ProductFilter filter, Pageable pageable);

    Page<SimpleProduct> findAll(Pageable pageable);

    List<ProductVariant> findVariantsByProductId(ULID slug);
    List<ProductAttribute> findAttributesByProductId(ULID slug);

    Optional<SimpleProduct> findBySlug(String slug);

    Optional<SimpleProduct> findById(ULID productId);

    List<ProductImage> findProductImage(ULID productId);

    List<ProductSpecs> findProductSpecs(ULID productId);

    Optional<SimpleVariant> findDefaultVariantForCart(ULID productId);

    Optional<SimpleVariant> findSimpleVariantById(ULID variantId);

    List<ProductVariant> findVariantsByIds(Collection<ULID> variantIds);

    List<SimpleVariant> findSimpleVariantsByIds(Collection<ULID> variantIds);

}
