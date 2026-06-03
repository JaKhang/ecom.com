package com.nlu.store.modules.order.admin.controller;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.jawire.Pagination;
import com.nlu.store.modules.order.models.Order;
import com.nlu.store.modules.order.services.OrderService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AdminOrderComponent extends Pagination<Order> {
    @Inject
    private OrderService orderService;

    @Override
    public Page<Order> getPage(Pageable pageable) {
        return orderService.find(pageable, null, null);
    }

    @Override
    public String view() {
        return "admin/order/table";
    }

    @Override
    public void rendering() {
        super.rendering();
        System.out.println(getData());
    }

    @Override
    protected Sort defaultSort() {
        return Sort.by(Sort.Direction.DESC, "o.created_at");
    }
}
