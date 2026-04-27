package com.ordertrack.orderdertrack.api.product.controller;

import com.ordertrack.orderdertrack.api.product.model.dto.ProductPublicResponse;
import com.ordertrack.orderdertrack.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductPublicController {

    private final ProductService productService;

    @GetMapping
    public List<ProductPublicResponse> getAllActive() {
        return productService.getAllActive();
    }

    @GetMapping("/{id}")
    public ProductPublicResponse getActiveById(@PathVariable Long id) {
        return productService.getActiveById(id);
    }
}