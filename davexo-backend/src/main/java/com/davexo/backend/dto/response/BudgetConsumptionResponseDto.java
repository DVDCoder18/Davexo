package com.davexo.backend.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Current month consumption data for a budget")
public class BudgetConsumptionResponseDto {

    @Schema(description = "Budget identifier")
    private Integer budgetId;

    @Schema(description = "Configured budget amount")
    private BigDecimal budgetAmount;

    @Schema(description = "Amount spent during the current month")
    private BigDecimal spentAmount;

    @Schema(description = "Remaining budget amount. Can be negative when the budget is exceeded")
    private BigDecimal remainingAmount;

    @Schema(description = "Budget consumption percentage. Can exceed 100 when the budget is exceeded, and can be null if the budget amount is zero")
    private BigDecimal consumptionPercentage;
}