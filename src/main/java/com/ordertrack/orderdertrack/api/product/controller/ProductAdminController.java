package com.ordertrack.orderdertrack.api.product.controller;

import com.ordertrack.orderdertrack.api.product.model.dto.ProductAdminResponse;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductCreateRequest;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductUpdateRequest;
import com.ordertrack.orderdertrack.api.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    @GetMapping
    public List<ProductAdminResponse> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ProductAdminResponse getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductAdminResponse createProduct(@RequestBody @Valid ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public ProductAdminResponse updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductUpdateRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    @PatchMapping("/{id}/activate")
    public ProductAdminResponse activateProduct(@PathVariable Long id) {
        return productService.activateProduct(id);
    }

    @PatchMapping("/{id}/deactivate")
    public ProductAdminResponse deactivateProduct(@PathVariable Long id) {
        return productService.deactivateProduct(id);
    }
}