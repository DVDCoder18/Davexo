package com.davexo.backend.dto.response.statistics;

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
@Schema(description = "Expense statistics for a requested period")
public class ExpenseStatisticsResponseDto {

    @NotNull
    @Schema(description = "Total amount spent during the requested period")
    private BigDecimal totalSpent;

    @Schema(description = "Expense evolution percentage compared with the previous comparable period, or null when no comparison can be calculated", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private BigDecimal evolutionPercentage;

    @Schema(description = "Historical monthly expense average based on completed months, or null when insufficient history exists", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private BigDecimal historicalMonthlyAverage;

    @NotNull
    @Schema(description = "Expense breakdown by category")
    private List<ExpenseCategoryBreakdownResponseDto> categoryBreakdown;
}