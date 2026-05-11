package com.ordertrack.orderdertrack.api.orderline.mapper;


import com.ordertrack.orderdertrack.api.orderline.model.dto.OrderLineResponse;
import com.ordertrack.orderdertrack.api.orderline.model.entity.OrderLine;
import org.springframework.stereotype.Component;

@Component
public class OrderLineMapper {


    public OrderLineResponse toResponse (OrderLine orderLine){

        return new OrderLineResponse(orderLine.getId(), orderLine.getOrder().getId(),orderLine.getProduct().getId(),
                orderLine.getName(), orderLine.getUnitPrice(),orderLine.getQuantity(), orderLine.getLineTotal() );
    }
}
