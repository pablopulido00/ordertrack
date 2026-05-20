package com.ordertrack.orderdertrack.api.user.repository;

import com.ordertrack.orderdertrack.api.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail (String email);

    //findByEmail
}
