package com.davexo.backend.service.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.response.statistics.TaskPriorityBreakdownResponseDto;
import com.davexo.backend.dto.response.statistics.TaskStatisticsResponseDto;
import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.repository.TaskRepository;
import com.davexo.backend.util.StatisticsPeriodUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskStatisticsService {

    private static final int PERCENTAGE_CALCULATION_SCALE = 4;
    private static final int DISPLAY_SCALE = 2;

    private final TaskRepository taskRepository;
    private final StatisticsPeriodUtils statisticsPeriodUtils;

    public TaskStatisticsResponseDto getTaskStatistics(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate) {

        LocalDate effectiveEndDate = statisticsPeriodUtils.getEffectiveEndDate(
                startDate,
                endDate);

        BigDecimal completionRate = calculateTaskCompletionRate(
                userId,
                startDate,
                effectiveEndDate);

        BigDecimal deadlineRespectRate = calculateDeadlineRespectRate(
                userId,
                startDate,
                effectiveEndDate);

        BigDecimal averageCompletionTimeDays = calculateAverageCompletionTimeDays(
                userId,
                startDate,
                effectiveEndDate);

        List<TaskPriorityBreakdownResponseDto> priorityBreakdown = calculateTaskPriorityBreakdown(
                userId,
                startDate,
                effectiveEndDate);

        return TaskStatisticsResponseDto.builder()
                .completionRate(completionRate)
                .deadlineRespectRate(deadlineRespectRate)
                .averageCompletionTimeDays(averageCompletionTimeDays)
                .priorityBreakdown(priorityBreakdown)
                .build();
    }

    private BigDecimal calculateTaskCompletionRate(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate) {

        long plannedTasks = taskRepository.countPlannedTasksByPeriod(
                userId,
                startDate,
                endDate);

        if (plannedTasks == 0) {
            return null;
        }

        long completedTasks = taskRepository.countCompletedPlannedTasksByPeriod(
                userId,
                startDate,
                endDate);

        return BigDecimal.valueOf(completedTasks)
                .divide(
                        BigDecimal.valueOf(plannedTasks),
                        PERCENTAGE_CALCULATION_SCALE,
                        RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(
                        DISPLAY_SCALE,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDeadlineRespectRate(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate) {

        long completedPlannedTasks = taskRepository.countCompletedPlannedTasksByPeriod(
                userId,
                startDate,
                endDate);

        if (completedPlannedTasks == 0) {
            return null;
        }

        long completedOnTimeTasks = taskRepository.countCompletedOnTimeTasksByPeriod(
                userId,
                startDate,
                endDate);

        return BigDecimal.valueOf(completedOnTimeTasks)
                .divide(
                        BigDecimal.valueOf(completedPlannedTasks),
                        PERCENTAGE_CALCULATION_SCALE,
                        RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(
                        DISPLAY_SCALE,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAverageCompletionTimeDays(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate) {

        List<Object[]> taskDates = taskRepository.findCompletionDatesByPeriod(
                userId,
                startDate,
                endDate);

        if (taskDates.isEmpty()) {
            return null;
        }

        long totalCompletionDays = taskDates.stream()
                .mapToLong(row -> {
                    LocalDate createdAt = (LocalDate) row[0];
                    LocalDate completedAt = (LocalDate) row[1];

                    return ChronoUnit.DAYS.between(
                            createdAt,
                            completedAt);
                })
                .sum();

        return BigDecimal.valueOf(totalCompletionDays)
                .divide(
                        BigDecimal.valueOf(taskDates.size()),
                        DISPLAY_SCALE,
                        RoundingMode.HALF_UP);
    }

    private List<TaskPriorityBreakdownResponseDto> calculateTaskPriorityBreakdown(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate) {

        List<Object[]> priorityCounts = taskRepository.countCompletedTasksByPriorityAndPeriod(
                userId,
                startDate,
                endDate);

        long totalCompletedTasks = priorityCounts.stream()
                .mapToLong(row -> (Long) row[1])
                .sum();

        return priorityCounts.stream()
                .map(row -> buildTaskPriorityBreakdown(
                        row,
                        totalCompletedTasks))
                .toList();
    }

    private TaskPriorityBreakdownResponseDto buildTaskPriorityBreakdown(
            Object[] row,
            long totalCompletedTasks) {

        TaskPriority priority = (TaskPriority) row[0];

        long count = (Long) row[1];

        BigDecimal percentage = BigDecimal.ZERO;

        if (totalCompletedTasks > 0) {
            percentage = BigDecimal.valueOf(count)
                    .divide(
                            BigDecimal.valueOf(totalCompletedTasks),
                            PERCENTAGE_CALCULATION_SCALE,
                            RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(
                            DISPLAY_SCALE,
                            RoundingMode.HALF_UP);
        }

        return TaskPriorityBreakdownResponseDto.builder()
                .priority(priority)
                .count((int) count)
                .percentage(percentage)
                .build();
    }
}