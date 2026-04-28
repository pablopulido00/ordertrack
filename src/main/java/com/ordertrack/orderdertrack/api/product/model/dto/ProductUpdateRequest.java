package com.ordertrack.orderdertrack.api.product.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(

        @Size(max = 255)
        String name,

        @DecimalMin("0.00")
        BigDecimal price,

        @PositiveOrZero
        Integer stock


)
{ }
