package com.davexo.backend.exception;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API error response")
public class CustomErrorResponse {

    @Schema(description = "HTTP status code", requiredMode = Schema.RequiredMode.REQUIRED)
    private int status;

    @NotNull
    @Schema(description = "Error type")
    private String error;

    @NotNull
    @Schema(description = "Human-readable error message")
    private String message;

    @NotNull
    @Schema(description = "Date and time when the error occurred")
    private LocalDateTime timestamp;
}
