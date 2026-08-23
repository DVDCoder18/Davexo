package com.davexo.backend.dto.response;

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
@Schema(description = "Current month consumption data for a budget")
public class BudgetConsumptionResponseDto {

    @NotNull
    @Schema(description = "Budget identifier")
    private Integer budgetId;

    @NotNull
    @Schema(description = "Configured budget amount")
    private BigDecimal budgetAmount;

    @NotNull
    @Schema(description = "Amount spent during the current month")
    private BigDecimal spentAmount;

    @NotNull
    @Schema(description = "Remaining budget amount. Can be negative when the budget is exceeded")
    private BigDecimal remainingAmount;

    @Schema(description = "Budget consumption percentage. Can exceed 100 when the budget is exceeded and can be null if the budget amount is zero", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private BigDecimal consumptionPercentage;
}