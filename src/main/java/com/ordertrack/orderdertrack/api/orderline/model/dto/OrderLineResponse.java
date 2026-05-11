package com.ordertrack.orderdertrack.api.orderline.model.dto;

import java.math.BigDecimal;

public record OrderLineResponse(

        Long id,

        Long orderId,

        Long productId,

        String productName,

        BigDecimal unitPrice,

        Integer quantity,

        BigDecimal lineTotal
) {
}