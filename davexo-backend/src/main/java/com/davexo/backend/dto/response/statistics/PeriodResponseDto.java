package com.davexo.backend.dto.response.statistics;

import java.time.LocalDate;

import com.davexo.backend.enums.StatisticsPeriod;

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
public class PeriodResponseDto {

    private StatisticsPeriod periodType;

    private LocalDate startDate;

    private LocalDate endDate;
}
