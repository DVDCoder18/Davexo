package com.davexo.backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomErrorResponse> handleBusinessException(BusinessException businessException) {

        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Business Exception",
                businessException.getMessage(),
                LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(customErrorResponse);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException resourceNotFoundException) {
        
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "ResourceNotFound Exception",
            resourceNotFoundException.getMessage(),
            LocalDateTime.now()
        );

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(customErrorResponse);
    }


}
