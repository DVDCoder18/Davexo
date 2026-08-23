package com.davexo.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Expense data")
public class ExpenseResponseDto {

    @NotNull
    @Schema(description = "Expense identifier")
    private Integer id;

    @NotNull
    @Schema(description = "Expense label")
    private String label;

    @NotNull
    @Schema(description = "Expense amount")
    private BigDecimal amount;

    @Schema(description = "Optional expense note", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String note;

    @NotNull
    @Schema(description = "Expense date")
    private LocalDate expenseDate;

    @NotNull
    @Schema(description = "Expense category identifier")
    private Integer expenseCategoryId;

    @NotNull
    @Schema(description = "Expense category name")
    private String expenseCategoryName;
}