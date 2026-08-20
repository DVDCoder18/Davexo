package com.davexo.backend.service.statistics;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.response.statistics.ExpenseStatisticsResponseDto;
import com.davexo.backend.dto.response.statistics.PeriodResponseDto;
import com.davexo.backend.dto.response.statistics.StatisticsResponseDto;
import com.davexo.backend.dto.response.statistics.TaskStatisticsResponseDto;
import com.davexo.backend.enums.StatisticsPeriod;
import com.davexo.backend.util.StatisticsPeriodUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ExpenseStatisticsService expenseStatisticsService;
    private final TaskStatisticsService taskStatisticsService;

    public StatisticsResponseDto getStatistics(
            Integer userId,
            StatisticsPeriod periodType,
            LocalDate startDate,
            LocalDate endDate) {

        StatisticsPeriodUtils.validatePeriod(
                periodType,
                startDate,
                endDate);

        ExpenseStatisticsResponseDto expenseStatistics = expenseStatisticsService.getExpenseStatistics(
                userId,
                periodType,
                startDate,
                endDate);

        TaskStatisticsResponseDto taskStatistics = taskStatisticsService.getTaskStatistics(
                userId,
                startDate,
                endDate);

        PeriodResponseDto period = PeriodResponseDto.builder()
                .periodType(periodType)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return StatisticsResponseDto.builder()
                .period(period)
                .expenses(expenseStatistics)
                .tasks(taskStatistics)
                .build();
    }
}