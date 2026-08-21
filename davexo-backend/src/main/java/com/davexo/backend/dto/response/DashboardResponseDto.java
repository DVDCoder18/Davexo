package com.davexo.backend.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponseDto {

    private BigDecimal currentMonthExpensesTotal;
    
    private long totalTasksToDo;

    private long totalShoppingItems;

    private BudgetConsumptionResponseDto globalBudgetConsumption;

    private List<TaskResponseDto> tasksToDo;

    private List<InventoryItemResponseDto> shoppingItems;

    private List<ExpenseResponseDto> recentExpenses; 
}
