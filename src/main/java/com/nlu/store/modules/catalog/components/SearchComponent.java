package com.nlu.store.modules.catalog.components;

import com.nlu.store.core.data.PageRequest;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jawire.Component;
import com.nlu.store.core.jawire.Model;
import com.nlu.store.modules.catalog.dto.ProductFilter;
import com.nlu.store.modules.catalog.model.SimpleCategory;
import com.nlu.store.modules.catalog.model.SimpleProduct;
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
public class SearchComponent extends Component {

    @Inject
    private ProductService productService;
    @Inject
    private CategoryService categoryService;

    @Getter
    private List<SimpleProduct> simpleProducts = Collections.emptyList();
    @Getter
    private List<SimpleCategory> categories = Collections.emptyList();

    @Model
    @Setter
    @Getter
    private String keyword;

    @Model
    @Setter
    @Getter
    private String selectedCategory;

    @Override
    public void mount() {
        super.mount();
        categories = categoryService.findAll(Sort.by(Sort.Direction.ASC, "sort_order"));

    }

    @Override
    public void rendering() {
        super.rendering();
        ULID categoryId = null;
        if (!StringUtils.isBlank(selectedCategory)) {
           categoryId = ULID.from(selectedCategory);
        }
        if (!StringUtils.isBlank(keyword))
            simpleProducts = productService.searchAndFilter(keyword, new ProductFilter(
                    null,
                    null,
                    null,
                    categoryId
            ), new PageRequest(1, 5)).getContent();

    }

    @Override
    public String view() {
        return "common/search";
    }
}
