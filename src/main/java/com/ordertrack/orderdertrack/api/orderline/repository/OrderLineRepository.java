package com.ordertrack.orderdertrack.api.orderline.repository;

import com.ordertrack.orderdertrack.api.orderline.model.entity.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    List<OrderLine> findByOrderId(Long orderId);

    boolean existsByOrderIdAndProductId(Long orderId, Long productId);

}
