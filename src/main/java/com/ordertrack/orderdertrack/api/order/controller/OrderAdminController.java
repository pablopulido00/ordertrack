package com.ordertrack.orderdertrack.api.order.controller;


import com.ordertrack.orderdertrack.api.order.model.dto.OrderAdminResponse;
import com.ordertrack.orderdertrack.api.order.model.dto.OrderCreateRequest;
import com.ordertrack.orderdertrack.api.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class OrderAdminController {


    private final OrderService orderService;



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderAdminResponse createOrder (@Valid @RequestBody OrderCreateRequest request){
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderAdminResponse getOrderById(@PathVariable Long id){
        return orderService.getOrderById(id);
    }

    @PatchMapping("/{id}/confirm")
    public OrderAdminResponse confirmOrder (@PathVariable Long id){
        return orderService.confirmOrder(id);
    }

    @PatchMapping("/{id}/cancel")
    public OrderAdminResponse cancelOrder (@PathVariable Long id){
        return orderService.cancelOrder(id);
    }


    @GetMapping
    public List<OrderAdminResponse> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderAdminResponse> getOrdersByCustomerId (@PathVariable Long id){
        return orderService.getOrdersByCustomerId(id);
    }

}
