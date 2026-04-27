package com.ordertrack.orderdertrack.api.product.model.dto;

import java.math.BigDecimal;

public record ProductAdminResponse (

        Long id,
        String name,
        BigDecimal price,
        Integer stock,
        Boolean active


){}
