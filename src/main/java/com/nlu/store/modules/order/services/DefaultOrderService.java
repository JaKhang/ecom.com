package com.nlu.store.modules.order.services;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.order.dao.OrderDao;
import com.nlu.store.modules.order.models.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DefaultOrderService implements OrderService{
    private final OrderDao dao;

    @Inject
    public DefaultOrderService(OrderDao dao) {
        this.dao = dao;
    }

    @Override
    public Page<Order> findOrderByUserId(ULID userId, Pageable pageable) {
        return dao.findOrderByUserId(userId,pageable);
    }
}
