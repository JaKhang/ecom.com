package com.nlu.store.modules.order.client.controller;

import com.nlu.store.core.web.AbstractController;
import com.nlu.store.core.web.AlertType;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.core.web.HttpContext;
import com.nlu.store.core.web.bind.BindingResult;
import com.nlu.store.modules.catalog.model.SimpleVariant;
import com.nlu.store.modules.catalog.model.cart.Cart;
import com.nlu.store.modules.catalog.services.ProductService;
import com.nlu.store.modules.order.dto.CheckoutRequest;
import com.nlu.store.modules.order.services.CheckoutService;
import com.nlu.store.modules.order.services.CheckoutResult;
import com.nlu.store.modules.order.validators.CheckoutValidator;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;

import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = {
        "/thanh-toan",
        "/checkout"
})
public class CheckoutController extends AbstractController {

    @Inject
    private ProductService productService;

    @Inject
    private CheckoutService checkOutService;



    @Override
    protected void doGet(HttpContext ctx) {

        //refresh cart data
        Cart cart = getCart(ctx);
        List<SimpleVariant> variants = productService.findSimpleVariantsByIds(cart.getVariantIds());
        boolean isChanged = cart.refresh(variants);
        ctx.setAttribute("cart", cart);
        ctx.setAttribute("isChanged", isChanged);

        //Default Form value
        Authentication authentication = ctx.authentication();
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setEmail(authentication.info().get("email"));
        checkoutRequest.setFullName(authentication.info().get("fullName"));
        ctx.setAttribute(HttpContext.ATTR_FORM, checkoutRequest);
        ctx.setAttribute(HttpContext.ATTR_ERRORS, Collections.emptyMap());

        //Forward to view
        ctx.view("client/order/checkout");

    }

    @Override
    protected void doPost(HttpContext ctx) {
        BindingResult<CheckoutRequest> result = ctx.getBody(CheckoutRequest.class, new CheckoutValidator());
        if(result.hasError()){
            ctx.setAttribute("cart", getCart(ctx));
            ctx.view("client/order/checkout");
            return;
        }

        Cart cart = getCart(ctx);
        if (cart.isEmpty()) {
            String path = ctx.getMessage("routes.checkout", "checkout");
            ctx.alert(AlertType.INFO, "checkout.empty");
            ctx.redirect("/" + path);            return;
        }


        List<SimpleVariant> variants = productService.findSimpleVariantsByIds(cart.getVariantIds());
        boolean isChanged = cart.refresh(variants);
        if (isChanged) {
            String path = ctx.getMessage("routes.checkout", "checkout");
            ctx.alert(AlertType.WARNING, "checkout.price_changed");
            ctx.redirect("/" + path);
            return;
        }
        Authentication authentication = ctx.authentication();
        CheckoutResult checkout = checkOutService.checkout(cart, result.data(), authentication);
        ctx.redirect(checkout.getPaymentGetWay());
    }

    private Cart getCart(HttpContext ctx){
        Cart cart = (Cart) ctx.getSession(true).getAttribute(Cart.KEY);
        if (cart == null) {
            cart = new Cart();
            ctx.getSession(true).setAttribute(Cart.KEY, cart);
        }
        return cart;
    }
}
