package com.ordertrack.orderdertrack.api.order.repository;

import com.ordertrack.orderdertrack.api.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByCustomerId(Long id);
}
