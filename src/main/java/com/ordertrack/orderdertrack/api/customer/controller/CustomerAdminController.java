package com.ordertrack.orderdertrack.api.customer.controller;


import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerCreateRequest;
import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerResponse;
import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerUpdateRequest;
import com.ordertrack.orderdertrack.api.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/customers")
public class CustomerAdminController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponse> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById (@PathVariable Long id){
        return customerService.getCustomerById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer (@RequestBody @Valid CustomerCreateRequest request){

        return customerService.createCustomer(request);

    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer (@PathVariable Long id, @RequestBody @Valid CustomerUpdateRequest request){
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id){
        customerService.deleteCustomer(id);
    }




}
