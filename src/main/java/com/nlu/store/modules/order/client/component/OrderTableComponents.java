package com.nlu.store.modules.order.client.component;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.jawire.JawireUpdateException;
import com.nlu.store.core.jawire.Pagination;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.order.models.Order;
import com.nlu.store.modules.order.services.OrderService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class OrderTableComponents extends Pagination<Order> {
    @Inject
    private OrderService orderService;

    @Override
    public Page<Order> getPage(Pageable pageable) {
        Authentication authentication = getHttpContext().authentication();
        if (authentication == null) {
            throw new JawireUpdateException("unauthorized", 401, "Unauthorized");
        }
        return orderService.findOrderByUserId(authentication.id(), pageable);
    }

    @Override
    public String view() {
        return "order/order-table";
    }
}
