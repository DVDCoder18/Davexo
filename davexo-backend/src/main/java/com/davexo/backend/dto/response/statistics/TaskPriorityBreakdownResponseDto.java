package com.davexo.backend.dto.response.statistics;

import java.math.BigDecimal;

import com.davexo.backend.enums.TaskPriority;

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
public class TaskPriorityBreakdownResponseDto {

    private TaskPriority priority;

    private Integer count;

    private BigDecimal percentage;
    
}
