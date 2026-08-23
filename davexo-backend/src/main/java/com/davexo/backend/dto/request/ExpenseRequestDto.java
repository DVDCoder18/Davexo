package com.davexo.backend.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequestDto {

    @NotBlank
    @Size(max = 100)
    private String label;
    
    @NotNull
    @Digits(integer = 8, fraction = 2)
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Size(max = 255)
    @Schema(nullable = true)
    private String note;

    @Schema(nullable = true)
    private LocalDate expenseDate;

    @NotNull
    private Integer expenseCategoryId;
}
