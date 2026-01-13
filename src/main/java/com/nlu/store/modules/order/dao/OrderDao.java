package com.nlu.store.modules.order.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.order.models.Order;

public interface OrderDao {
    ULID createOrderAndDecreaseStock(Order order);

    Page<Order> findOrderByUserId(ULID userId, Pageable pageable);
}
