package com.ordertrack.orderdertrack.api.auth.model.dto;

import com.ordertrack.orderdertrack.api.user.model.enums.Role;

public record AuthResponse(


        Long id,
        String email,
        Role role,
        String token



) {
}
