package com.ordertrack.orderdertrack.api.order.mapper;


import com.ordertrack.orderdertrack.api.customer.model.entity.Customer;
import com.ordertrack.orderdertrack.api.order.model.dto.OrderAdminResponse;
import com.ordertrack.orderdertrack.api.order.model.dto.OrderPublicResponse;
import com.ordertrack.orderdertrack.api.order.model.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {


    public OrderAdminResponse toAdminResponse (Order order){

        return new OrderAdminResponse(order.getId(), order.getCustomer().getId(),
                order.getCustomer().getName(), order.getCustomer().getEmail(),
                order.getStatus(), order.getTotal(), order.getCreatedAt(), order.getUpdatedAt());
    }

    public OrderPublicResponse toPublicResponse (Order order){
        return new OrderPublicResponse(order.getId(), order.getStatus(),
                order.getTotal(), order.getCreatedAt(), order.getUpdatedAt());
    }
}
