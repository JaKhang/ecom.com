package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.model.Product;
import com.nlu.store.modules.catalog.model.ProductDetails;
import com.nlu.store.modules.catalog.dto.ProductRequest;

public interface ProductDAO {
    Page<Product> search(String keyword, Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    ProductDetails findProductDetailById(ULID id);

    ULID createProduct(ProductRequest productRequest);

    void updateProduct(ULID id, ProductRequest productRequest);

    void deleteProduct(ULID id);
}
