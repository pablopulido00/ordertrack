package com.ordertrack.orderdertrack.api.customer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CustomerCreateRequest (
        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 50)
        String phone,

        @NotBlank
        @Size(max = 255)
        String email


){}
