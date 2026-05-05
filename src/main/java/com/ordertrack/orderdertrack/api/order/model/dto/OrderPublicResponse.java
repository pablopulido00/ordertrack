package com.ordertrack.orderdertrack.api.order.model.dto;

import com.ordertrack.orderdertrack.api.order.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderPublicResponse (


        Long id,
        OrderStatus status,
        BigDecimal total,
        Instant createdAt,
        Instant updatedAt

)
{ }
