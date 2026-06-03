package com.nlu.store.modules.order.services;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.order.dao.OrderDao;
import com.nlu.store.modules.order.models.Order;
import com.nlu.store.modules.order.models.OrderStatus;
import com.nlu.store.modules.order.models.PaymentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class DefaultOrderService implements OrderService{
    private final OrderDao dao;

    @Inject
    public DefaultOrderService(OrderDao dao) {
        this.dao = dao;
    }

    @Override
    public Page<Order> findByUserId(ULID userId, Pageable pageable) {
        return dao.findOrderByUserId(userId,pageable);
    }

    @Override
    public Page<Order> find(Pageable pageable, OrderStatus status, PaymentStatus paymentStatus) {
        return dao.find(pageable, status, paymentStatus);
    }

    @Override
    public Optional<Order> findById(ULID id) {
        return dao.findById(id);
    }
}
