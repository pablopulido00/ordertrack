package com.ordertrack.orderdertrack.api.orderline.model.dto;

import jakarta.validation.constraints.Min;

public record OrderLineUpdateRequest(

        @Min(value= 1)
        Integer quantity
) { }
