package com.davexo.backend.dto.response.statistics;

import java.math.BigDecimal;
import java.util.List;

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
public class TaskStatisticsResponseDto {

    private BigDecimal completionRate;

    private BigDecimal deadlineRespectRate;

    private BigDecimal averageCompletionTimeDays;

    private List<TaskPriorityBreakdownResponseDto> priorityBreakdown;

}
