package com.ordertrack.orderdertrack.api.auth.service;


import com.ordertrack.orderdertrack.api.auth.model.dto.AuthResponse;
import com.ordertrack.orderdertrack.api.auth.model.dto.LoginRequest;
import com.ordertrack.orderdertrack.api.auth.model.dto.RegisterRequest;
import com.ordertrack.orderdertrack.api.security.CustomUserDetailsService;
import com.ordertrack.orderdertrack.api.user.model.entity.AppUser;
import com.ordertrack.orderdertrack.api.user.model.enums.Role;
import com.ordertrack.orderdertrack.api.user.repository.UserRepository;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;


    public AuthResponse register (RegisterRequest request){

        if(userRepository.existsByEmail(request.email())){
            throw new ConflictException("Email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        AppUser appUser = new AppUser(request.email(), passwordHash, Role.CUSTOMER);


        AppUser savedAppUser = userRepository.save(appUser);

        UserDetails  userDetails = customUserDetailsService.loadUserByUsername(savedAppUser.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(savedAppUser.getId(), savedAppUser.getEmail(), savedAppUser.getRole(), token);

    }


    @Transactional(readOnly = true)
      public AuthResponse login (LoginRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );


        AppUser user = userRepository.findByEmail(request.email())
                .orElseThrow(()-> new NotFoundException("User not found"));

        UserDetails  userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), token);


      }




}
