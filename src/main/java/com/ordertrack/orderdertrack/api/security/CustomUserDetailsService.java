package com.ordertrack.orderdertrack.api.security;


import com.ordertrack.orderdertrack.api.user.model.entity.AppUser;
import com.ordertrack.orderdertrack.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

   private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));

                return User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles(user.getRole().name())
                        .build();

    }


}
