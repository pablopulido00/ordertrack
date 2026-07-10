package com.ordertrack.orderdertrack.api.auth.model.dto;

import com.ordertrack.orderdertrack.api.user.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotNull
        Role role


) {
}
