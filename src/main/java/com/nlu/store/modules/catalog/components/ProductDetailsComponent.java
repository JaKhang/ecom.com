package com.nlu.store.modules.catalog.components;

import com.nlu.store.core.data.ULID;
import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.jawire.Action;
import com.nlu.store.core.jawire.Component;
import com.nlu.store.core.jawire.Model;
import com.nlu.store.modules.catalog.model.SimpleProduct;
import com.nlu.store.modules.catalog.model.details.*;
import com.nlu.store.modules.catalog.services.ProductService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@RequestScoped
public class ProductDetailsComponent extends Component {

    @Getter
    private SimpleProduct product;
    @Getter
    private ProductVariant selectedVariant;

    @Getter
    private List<ProductImage> gallery = Collections.emptyList();

    @Model
    @Setter
    @Getter
    private ULID productId;

    @Setter
    @Getter
    @Model
    private List<ProductAttribute> attributes;

    @Getter
    @Setter
    @Model
    private Map<String, String> selectedOptions = new HashMap<>();
    @Getter
    @Setter
    @Model
    private Set<String> disableOptions = Collections.emptySet();


    @Model
    @Getter
    @Setter
    private List<ProductVariant> variants = Collections.emptyList();

    @Override
    public String view() {
        return "shop/details";
    }

    @Override
    public void mount() {
        product = getHttpContext().getAttribute("product", SimpleProduct.class);
        if (product == null) {
            throw new ResourceNotFoundException("product.notfound");
        }
        this.productId = product.getId();
        this.attributes = productService.findAttributesByProductId(product.getId());
        variants = productService.findVariantsByProductId(product.getId());
        this.selectedVariant = variants.stream().filter(ProductVariant::isDefault).findFirst().orElseGet(() -> variants.isEmpty() ? null : variants.get(0));
        if (selectedVariant != null) {
            selectedVariant.getValues().forEach(
                    (v) -> selectedOptions.put(v.getAttrIdString(), v.getOptionId().toString())
            );
        }

        this.gallery = productService.findProductGallery(productId);
    }

    @Override
    public void hydrated() {
        System.out.println(productId);
    }

    @Action
    public void choose(String attId, String optionId) {
        selectedOptions.put(attId, optionId);
        if (selectedOptions.size() == attributes.size()) {
            selectedVariant = variants.stream().filter(
                    (variant) -> variant.getValues().stream().allMatch(
                            variantValue -> selectedOptions.getOrDefault(variantValue.getAttrIdString(), "").equals(variantValue.getOptIdString())
                    )
            ).findAny().orElse(null);

            if (selectedVariant == null) {
                selectedOptions.clear();
                selectedOptions.put(attId, optionId);
                disableOptions = getDisabledOptionIds(variants, attributes, ULID.from(attId), ULID.from(optionId));

            } else {
                disableOptions.clear();

            }
        }


    }

    @Override
    public void rendering() {
        product = productService.findById(productId).orElseThrow(() -> new ResourceNotFoundException("product.notfound"));
        super.rendering();

    }

    @Inject
    private ProductService productService;


    public static Set<String> getDisabledOptionIds(
            List<ProductVariant> allVariants,
            List<ProductAttribute> allAttributes,
            ULID selectedAttrId,
            ULID selectedOptId
    ) {
        Set<String> disabledOptionIds = new HashSet<>();

        // 1. Tìm các Variant hợp lệ (Chứa Option đang chọn & Còn hàng)
        List<ProductVariant> compatibleVariants = allVariants.stream()
                .filter(variant -> variant.getStocks() > 0) // Chỉ xét hàng còn trong kho
                .filter(variant -> variant.getValues().stream()
                        .anyMatch(val -> val.getAttributeId().equals(selectedAttrId)
                                && val.getOptionId().equals(selectedOptId)))
                .toList();

        // 2. Lấy ra tất cả các Option ID "đi kèm" có trong các variant hợp lệ này
        // (Đây là danh sách các option KHẢ DỤNG)
        Set<String> availableOptionIds = new HashSet<>();
        for (ProductVariant variant : compatibleVariants) {
            for (VariantValue val : variant.getValues()) {
                availableOptionIds.add(val.getOptionId().toString());
            }
        }

        // 3. So sánh với toàn bộ Option để tìm ra cái nào KHÔNG khả dụng
        for (ProductAttribute attr : allAttributes) {
            // (Tùy chọn) Không disable các option cùng cấp (VD: Chọn Đỏ thì không disable Xanh)
            // Nếu muốn disable cả anh em cùng cấp nếu hết hàng thì bỏ dòng if này đi.
            if (attr.getId().equals(selectedAttrId)) {
                continue;
            }

            for (AttributeOption opt : attr.getOptions()) {
                String optIdStr = opt.getId().toString();
                // Nếu option này không nằm trong danh sách khả dụng -> Thêm vào list Disabled
                if (!availableOptionIds.contains(optIdStr)) {
                    disabledOptionIds.add(optIdStr);
                }
            }
        }

        return disabledOptionIds;
    }
}
