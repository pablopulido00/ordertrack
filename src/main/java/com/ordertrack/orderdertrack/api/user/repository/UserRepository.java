package com.ordertrack.orderdertrack.api.user.repository;

import com.ordertrack.orderdertrack.api.user.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail (String email);

    //findByEmail
}
