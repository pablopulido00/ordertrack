package com.ordertrack.orderdertrack.api.product.model.dto;

import java.math.BigDecimal;

public record ProductPublicResponse (

        Long id,
        String name,
        BigDecimal price

){}
