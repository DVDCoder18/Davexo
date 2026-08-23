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
@Schema(description = "Task statistics for a requested period")
public class TaskStatisticsResponseDto {

    @Schema(description = "Task completion rate, or null when no planned task exists for the period", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private BigDecimal completionRate;

    @Schema(description = "Deadline respect rate, or null when no completed planned task exists", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private BigDecimal deadlineRespectRate;

    @Schema(description = "Average task completion time in days, or null when no completed task can be evaluated", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private BigDecimal averageCompletionTimeDays;

    @NotNull
    @Schema(description = "Completed task breakdown by priority")
    private List<TaskPriorityBreakdownResponseDto> priorityBreakdown;
    
}
