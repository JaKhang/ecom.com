package com.nlu.store.modules.order.services;

import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.catalog.model.cart.Cart;
import com.nlu.store.modules.order.dto.CheckoutRequest;

public interface CheckoutService {

    CheckoutResult checkout(Cart cart, CheckoutRequest request, Authentication authentication);
}
