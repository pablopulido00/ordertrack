package com.ordertrack.orderdertrack.common.exception;

public record ApiError (

    int status,
    String error,
    String message,
    String path
){}
