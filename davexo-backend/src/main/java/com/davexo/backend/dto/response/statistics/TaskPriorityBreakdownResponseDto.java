package com.davexo.backend.dto.response.statistics;

import java.math.BigDecimal;

import com.davexo.backend.enums.TaskPriority;

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
@Schema(description = "Completed task statistics for one priority")
public class TaskPriorityBreakdownResponseDto {

    @NotNull
    @Schema(description = "Task priority")
    private TaskPriority priority;

    @NotNull
    @Schema(description = "Number of completed tasks with this priority")
    private Integer count;

    @NotNull
    @Schema(description = "Percentage of completed tasks represented by this priority")
    private BigDecimal percentage;
}