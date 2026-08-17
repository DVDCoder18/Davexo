package com.davexo.backend.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
