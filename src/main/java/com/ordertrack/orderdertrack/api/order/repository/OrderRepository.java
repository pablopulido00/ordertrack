package com.ordertrack.orderdertrack.api.order.repository;

import com.ordertrack.orderdertrack.api.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
