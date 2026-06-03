package com.nlu.store.modules.order.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.order.models.Order;
import com.nlu.store.modules.order.models.OrderStatus;
import com.nlu.store.modules.order.models.PaymentStatus;

import java.util.Optional;

public interface OrderDao {
    ULID createOrderAndDecreaseStock(Order order);

    Page<Order> findOrderByUserId(ULID userId, Pageable pageable);

    void paymentSuccess(String transactionId, String orderRef);

    Page<Order> find(Pageable pageable, OrderStatus status, PaymentStatus paymentStatus);

    Optional<Order> findById(ULID id);
}
