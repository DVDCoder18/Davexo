package com.davexo.backend.dto.response;

import java.math.BigDecimal;

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
public class BudgetConsumptionResponseDto {

    private Integer budgetId;

    private BigDecimal budgetAmount;
    
    private BigDecimal spentAmount;

    private BigDecimal remainingAmount;
    
    private BigDecimal consumptionPercentage;
}
