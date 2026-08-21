package com.davexo.backend.dto.request;

import java.math.BigDecimal;
import java.util.List;

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
@Schema(description = "Request used to create or update a budget")
public class BudgetRequestDto {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Budget name")
    private String name;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @DecimalMin(value = "0.01")
    @Schema(description = "Budget amount, strictly greater than 0")
    private BigDecimal amount;

    @NotNull
    @Schema(description = "Expense category IDs followed by the budget. An empty list creates a GLOBAL budget; a non-empty list creates a SELECTED_CATEGORIES budget")
    private List<Integer> followedCategoryIds;
}