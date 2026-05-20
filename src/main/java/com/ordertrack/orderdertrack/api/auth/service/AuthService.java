package com.ordertrack.orderdertrack.api.auth.service;


import com.ordertrack.orderdertrack.api.auth.model.dto.AuthResponse;
import com.ordertrack.orderdertrack.api.auth.model.dto.RegisterRequest;
import com.ordertrack.orderdertrack.api.user.model.entity.User;
import com.ordertrack.orderdertrack.api.user.repository.UserRepository;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthResponse register (RegisterRequest request){

        if(userRepository.existsByEmail(request.email())){
            throw new ConflictException("Email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(request.email(), passwordHash, request.role());


        User savedUser = userRepository.save(user);

        return new AuthResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());



    }

}
