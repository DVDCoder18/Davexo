package com.davexo.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

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
@Schema(description = "Aggregated dashboard data for the authenticated user")
public class DashboardResponseDto {

    @NotNull
    @Schema(description = "Total amount spent by the user during the current month")
    private BigDecimal currentMonthExpensesTotal;

    @Schema(description = "Total number of tasks currently in TO_DO status", requiredMode = Schema.RequiredMode.REQUIRED)
    private long totalTasksToDo;

    @Schema(description = "Total number of inventory items currently included in the shopping list", requiredMode = Schema.RequiredMode.REQUIRED)
    private long totalShoppingItems;

    @Schema(description = "Current consumption of the user's global budget, or null if no global budget exists", requiredMode = Schema.RequiredMode.REQUIRED)
    private BudgetConsumptionResponseDto globalBudgetConsumption;

    @NotNull
    @Schema(description = "Up to 5 TO_DO tasks ordered by business priority and due date")
    private List<TaskResponseDto> tasksToDo;

    @NotNull
    @Schema(description = "Up to 5 shopping list items ordered by shopping priority")
    private List<InventoryItemResponseDto> shoppingItems;

    @NotNull
    @Schema(description = "The 10 most recent expenses, ordered by expense date descending")
    private List<ExpenseResponseDto> recentExpenses;
}