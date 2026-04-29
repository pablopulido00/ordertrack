package com.ordertrack.orderdertrack.api.customer.mapper;

import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerResponse;
import com.ordertrack.orderdertrack.api.customer.model.entity.Customer;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductPublicResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse (Customer customer){
        return new CustomerResponse(customer.getId(), customer.getUser_id(), customer.getName(), customer.getPhone(), customer.getEmail() );
    }

}
