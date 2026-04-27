package com.ordertrack.orderdertrack.api.product.model.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateRequest (

        @NotBlank
        @Size(max = 255)
        String name,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal price,

        @NotNull
        @PositiveOrZero
        Integer stock,

        @NotNull
        Boolean active

) {}
