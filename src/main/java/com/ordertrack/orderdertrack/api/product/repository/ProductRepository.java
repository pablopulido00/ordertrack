package com.ordertrack.orderdertrack.api.product.repository;

import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {


    List<Product> findByActiveTrue();
}
