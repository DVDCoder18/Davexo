package com.davexo.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.davexo.backend.enums.BudgetScope;

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
@Schema(description = "Budget data")
public class BudgetResponseDto {

    @NotNull
    @Schema(description = "Budget identifier")
    private Integer id;

    @NotNull
    @Schema(description = "Budget name")
    private String name;

    @NotNull
    @Schema(description = "Configured budget amount")
    private BigDecimal amount;

    @NotNull
    @Schema(description = "Budget scope")
    private BudgetScope scope;

    @NotNull
    @Schema(description = "Expense category IDs followed by the budget. Empty for a global budget")
    private List<Integer> followedCategoryIds;
}