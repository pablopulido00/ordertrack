package com.ordertrack.orderdertrack.api.auth.controller;


import com.ordertrack.orderdertrack.api.auth.model.dto.AuthResponse;
import com.ordertrack.orderdertrack.api.auth.model.dto.LoginRequest;
import com.ordertrack.orderdertrack.api.auth.model.dto.RegisterRequest;
import com.ordertrack.orderdertrack.api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {


    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register (@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login (@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

}
