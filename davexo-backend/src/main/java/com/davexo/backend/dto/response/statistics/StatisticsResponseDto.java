package com.davexo.backend.dto.response.statistics;

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
@Schema(description = "Aggregated expense and task statistics for a requested period")
public class StatisticsResponseDto {

    @NotNull
    @Schema(description = "Effective statistics period")
    private PeriodResponseDto period;

    @NotNull
    @Schema(description = "Expense statistics calculated for the period")
    private ExpenseStatisticsResponseDto expenses;

    @NotNull
    @Schema(description = "Task statistics calculated for the period")
    private TaskStatisticsResponseDto tasks;
}