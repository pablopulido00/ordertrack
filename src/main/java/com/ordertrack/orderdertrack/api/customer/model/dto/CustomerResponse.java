package com.ordertrack.orderdertrack.api.customer.model.dto;

public record CustomerResponse(

           Long id,
           Long user_id,
           String name,
           String phone,
           String email


) {}
