package com.nlu.store.modules.catalog.components;

import com.nlu.store.core.data.ULID;
import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.jawire.Action;
import com.nlu.store.core.web.BadRequestException;
import com.nlu.store.modules.catalog.model.cart.Cart;
import com.nlu.store.modules.catalog.model.cart.CartItem;
import com.nlu.store.modules.catalog.model.SimpleVariant;
import com.nlu.store.modules.catalog.services.ProductService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class HeaderCartComponent extends CartComponent {

    private static final String CART_KEY = "SESSION_CART";

    @Inject
    private ProductService productService;

    @Action
    public void addDefault(String productId) {
        SimpleVariant variant = productService.findDefaultVariantForCart(ULID.from(productId)).orElseThrow(() -> new ResourceNotFoundException("variant.notfound"));
        if (variant.getStocks() <= 0) {
            throw new BadRequestException("variant.sold_out");
        }
        Cart cart = getCart();
        CartItem item = CartItem.builder()
                .variantId(variant.getId())
                .quantity(1)
                .productId(variant.getProductId())
                .unitPrice(variant.getPrice())
                .unitOriginalPrice(variant.getOriginalPrice())
                .variantName(variant.getName())
                .thumbnail(variant.getThumbnail())
                .build();
        cart.add(item);
    }






    @Override
    public String view() {
        return "common/cart";
    }
}
