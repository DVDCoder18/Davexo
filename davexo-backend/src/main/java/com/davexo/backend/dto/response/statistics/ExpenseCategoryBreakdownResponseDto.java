package com.davexo.backend.dto.response.statistics;

import java.math.BigDecimal;

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
@Schema(description = "Expense statistics breakdown for one category")
public class ExpenseCategoryBreakdownResponseDto {

    @NotNull
    @Schema(description = "Expense category identifier")
    private Integer categoryId;

    @NotNull
    @Schema(description = "Expense category name")
    private String categoryName;

    @NotNull
    @Schema(description = "Total amount spent in the category")
    private BigDecimal totalAmount;

    @NotNull
    @Schema(description = "Percentage of total expenses represented by the category")
    private BigDecimal percentage;
}