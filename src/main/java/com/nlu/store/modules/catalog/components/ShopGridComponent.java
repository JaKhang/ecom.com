package com.nlu.store.modules.catalog.components;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jawire.Model;
import com.nlu.store.core.jawire.Pagination;
import com.nlu.store.modules.catalog.dto.ProductFilter;
import com.nlu.store.modules.catalog.model.Brand;
import com.nlu.store.modules.catalog.model.SimpleCategory;
import com.nlu.store.modules.catalog.model.SimpleProduct;
import com.nlu.store.modules.catalog.services.BrandService;
import com.nlu.store.modules.catalog.services.CategoryService;
import com.nlu.store.modules.catalog.services.ProductService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

@RequestScoped
public class ShopGridComponent extends Pagination<SimpleProduct> {
    /*------------------
            Model
    --------------------*/
    @Setter
    @Getter
    @Model
    private String keyword;

    /*------------------
          Transient
    --------------------*/

    @Getter
    private List<SimpleCategory> categories = Collections.emptyList();

    @Getter
    @Model
    @Setter
    private SimpleCategory selectedCategory;

    @Getter
    private List<Brand> brands = Collections.emptyList();

    @Model
    @Getter
    @Setter
    private List<String> selectedBrandIds = Collections.emptyList();

    @Inject
    private ProductService productService;
    @Inject
    private CategoryService categoryService;
    @Inject
    private BrandService brandService;

    @Override
    public Page<SimpleProduct> getPage(Pageable pageable) {
        Page<SimpleProduct> simpleProducts;
        if (StringUtils.isBlank(keyword) && selectedBrandIds.isEmpty() && selectedCategory == null) {
            simpleProducts = productService.findAll(pageable);
        } else {
            simpleProducts = productService.searchAndFilter(keyword, new ProductFilter(
                    selectedBrandIds.stream().map(ULID::from).toList(),
                    null,
                    null,
                    selectedCategory == null ? null : selectedCategory.getId()
            ), pageable);
        }
        return simpleProducts;

    }

    @Override
    protected void clear() {
        selectedBrandIds = Collections.emptyList();
        selectedCategory = null;
        keyword = null;
    }

    @Override
    public String view() {
        return "shop/grid";
    }

    @Override
    protected Sort defaultSort() {
        return Sort.by(Sort.Direction.DESC, "release_date");
    }

    @Override
    public void mount() {
        super.mount();
        keyword = this.getHttpContext().getParam("keyword", "", false);
        selectedCategory = this.getHttpContext().getAttribute("category", SimpleCategory.class);
        categories = categoryService.findAll(Sort.by(Sort.Direction.ASC, "sort_order"));
        System.out.println(selectedCategory);
    }

    @Override
    public void rendering() {
        super.rendering();
        brands = brandService.findAll(Sort.by(Sort.Direction.ASC, "sort_order"));
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ShopGridComponent{");
        sb.append("keyword='").append(keyword).append('\'');
        sb.append(", categories=").append(categories);
        sb.append(", selectedCategory=").append(selectedCategory);
        sb.append(", brands=").append(brands);
        sb.append(", selectedBrandIds=").append(selectedBrandIds);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public void updated(String field, Object value) {
        setPage(1);
    }
}
