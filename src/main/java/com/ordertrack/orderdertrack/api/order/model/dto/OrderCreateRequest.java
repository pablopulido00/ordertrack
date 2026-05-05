package com.ordertrack.orderdertrack.api.order.model.dto;

import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest (

        @NotNull
        Long customerId



)

{ }
