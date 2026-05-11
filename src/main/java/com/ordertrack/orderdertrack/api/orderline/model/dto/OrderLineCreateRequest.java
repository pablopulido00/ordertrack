package com.ordertrack.orderdertrack.api.orderline.model.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;



public record OrderLineCreateRequest(


        @NotNull
        Long productId,

        @NotNull
        @Min(value= 1)
        Integer quantity



){}
