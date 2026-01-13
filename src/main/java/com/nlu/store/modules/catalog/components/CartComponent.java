package com.nlu.store.modules.catalog.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.exceptions.ResourceNotFoundException;
import com.nlu.store.core.jawire.Action;
import com.nlu.store.core.jawire.Component;
import com.nlu.store.core.jawire.JawireUpdateException;
import com.nlu.store.core.web.BadRequestException;
import com.nlu.store.modules.catalog.model.cart.Cart;
import com.nlu.store.modules.catalog.model.cart.CartItem;
import com.nlu.store.modules.catalog.model.SimpleVariant;
import com.nlu.store.modules.catalog.services.ProductService;
import jakarta.inject.Inject;


public abstract class CartComponent extends Component {

    @Inject
    private ProductService productService;

    @JsonIgnore
    public Cart getCart() {
        Cart cart = (Cart) getHttpContext().getSession(true).getAttribute(Cart.KEY);
        if (cart == null) {
            cart = new Cart();
            getHttpContext().getSession(true).setAttribute(Cart.KEY, cart);
        }
        return cart;
    }



    @Action
    public void remove(ULID variantId){
        Cart cart = this.getCart();
        cart.remove(variantId);
    }

    @Action
    public void updateCart(String variantId, Integer quantity) {
        if (quantity == null || quantity < 0) {
            quantity = 0;
        }

        ULID vId = ULID.from(variantId);
        Cart cart = getCart();

        if (quantity > 0) {
            SimpleVariant variant = productService.findSimpleVariant(vId)
                    .orElseThrow(() ->  new JawireUpdateException("variant.notfound", 404, "NotFound"));
            if (variant.getStocks() < quantity) {
                throw new JawireUpdateException("variant.insufficient_stock", 400, "BadRequest");
            }
        }

        cart.update(vId, quantity);
    }


    @Action
    public void addOrUpdate(String variantId, Integer quantity) {
        if (quantity == null || quantity <= 0) return;

        ULID vId = ULID.from(variantId);
        Cart cart = getCart();


        SimpleVariant variant = productService.findSimpleVariant(vId)
                .orElseThrow(() -> new JawireUpdateException("variant.notfound", 404, "NotFound"));

        int currentInCart = cart.getQuantity(vId);

        if (variant.getStocks() < (currentInCart + quantity)) {
            throw new JawireUpdateException("variant.insufficient_stock", 400, "BadRequest");
        }

        CartItem item = CartItem.builder()
                .variantId(variant.getId())
                .productId(variant.getProductId())
                .variantName(variant.getName())
                .sku(variant.getSku())
                .thumbnail(variant.getThumbnail())
                .unitPrice(variant.getPrice())
                .unitOriginalPrice(variant.getOriginalPrice())
                .reviewsCount(variant.getReviewsCount())
                .ratingAvg(variant.getRatingAvg())
                .inStock(true)
                .slug(variant.getSlug())
                .quantity(quantity)
                .build();

        cart.add(item);
    }

    @Action
    public void refresh(){

    }
}
