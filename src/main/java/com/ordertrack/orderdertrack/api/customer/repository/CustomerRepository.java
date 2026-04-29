package com.ordertrack.orderdertrack.api.customer.repository;


import com.ordertrack.orderdertrack.api.customer.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository <Customer, Long> {

    boolean existsByEmail(String email);
}
