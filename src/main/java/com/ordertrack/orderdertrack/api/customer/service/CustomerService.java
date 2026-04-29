package com.ordertrack.orderdertrack.api.customer.service;


import com.ordertrack.orderdertrack.api.customer.mapper.CustomerMapper;
import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerCreateRequest;
import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerResponse;
import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerUpdateRequest;
import com.ordertrack.orderdertrack.api.customer.model.entity.Customer;
import com.ordertrack.orderdertrack.api.customer.repository.CustomerRepository;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {


    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;




    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers (){
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }



    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id){

        Customer customer = findCustomerOrThrow(id);

        return customerMapper.toResponse(customer);

    }


    public CustomerResponse createCustomer (CustomerCreateRequest request){


        if (customerRepository.existsByEmail(request.email())){
            throw new ConflictException("Customer email already exists");
        }

        Customer customer = new Customer(
                request.name(),
                request.phone(),
                request.email()
        );


        Customer customerSaved = customerRepository.save(customer);

        return customerMapper.toResponse(customerSaved);


    }

    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request){

        Customer customer = findCustomerOrThrow(id);

        if(request.name() != null){
            customer.setName(request.name());
        }


        if(request.phone() != null){
            customer.setPhone(request.phone());
        }

        if(request.email() != null && !request.email().equals(customer.getEmail())){
            if(customerRepository.existsByEmail(request.email())){
                throw new ConflictException("Customer email already exists");
            }
            customer.setEmail(request.email());
        }


        Customer customerUpdated  = customerRepository.save(customer);

        return customerMapper.toResponse(customerUpdated);



    }

    public void deleteCustomer (Long id){

        Customer customer = findCustomerOrThrow(id);

        customerRepository.delete(customer);
    }



    private Customer findCustomerOrThrow(Long id){
    return customerRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("Customer with id " + id + "does not exist" ));
    }




}

