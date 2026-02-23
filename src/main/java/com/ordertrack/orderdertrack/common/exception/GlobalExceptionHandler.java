package com.ordertrack.orderdertrack.common.exception;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

     private ResponseEntity<ApiError> build(HttpStatus status, String message, String path) {
         ApiError body = new ApiError(
                 status.value(),
                 status.getReasonPhrase(),
                 message,
                 path
         );
         return ResponseEntity.status(status).body(body);

     }

     @ExceptionHandler(NotFoundException.class)
       public ResponseEntity<ApiError> handleNotFound(
             NotFoundException ex, HttpServletRequest request
     ){
         return build(HttpStatus.NOT_FOUND,
                 ex.getMessage(),
                 request.getRequestURI());
     }

     @ExceptionHandler(ConflictException.class)
       public ResponseEntity<ApiError> handleConflictExcepion(
               ConflictException ex,
               HttpServletRequest request
     )  {
         return build(
                 HttpStatus.CONFLICT,
                 ex.getMessage(),
                 request.getRequestURI()
         );
     }

     @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiError> handleBadRequest(
             BadRequestException ex,
             HttpServletRequest request
     ) {

         return build(
           HttpStatus.BAD_REQUEST,
           ex.getMessage(),
           request.getRequestURI()
         );
        }



        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidation(
                MethodArgumentNotValidException ex,
                HttpServletRequest request
        ){
            return build(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage(),
                    request.getRequestURI()
            );
        }

    }




