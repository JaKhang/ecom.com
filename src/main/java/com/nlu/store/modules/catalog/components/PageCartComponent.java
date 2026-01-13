package com.nlu.store.modules.catalog.components;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class PageCartComponent extends CartComponent {
    @Override
    public String view() {
        return "shop/cart";
    }
}
