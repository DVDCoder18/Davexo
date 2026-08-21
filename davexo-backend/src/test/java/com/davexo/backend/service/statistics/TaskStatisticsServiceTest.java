package com.davexo.backend.service.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.davexo.backend.dto.response.statistics.TaskStatisticsResponseDto;
import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.repository.TaskRepository;
import com.davexo.backend.util.StatisticsPeriodUtils;

@ExtendWith(MockitoExtension.class)
class TaskStatisticsServiceTest {

    private static final Integer USER_ID = 1;

    @Mock
    private TaskRepository taskRepository;

    private TaskStatisticsService taskStatisticsService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-08-20T12:00:00Z"),
                ZoneId.of("Europe/Paris"));

        StatisticsPeriodUtils statisticsPeriodUtils = new StatisticsPeriodUtils(fixedClock);

        taskStatisticsService = new TaskStatisticsService(
                taskRepository,
                statisticsPeriodUtils);
    }

    @Test
    void getTaskStatistics_shouldCalculateCompletionRate() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(taskRepository.countPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(8L);

        when(taskRepository.countCompletedPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(6L);

        when(taskRepository.countCompletedOnTimeTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(4L);

        when(taskRepository.findCompletionDatesByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        when(taskRepository.countCompletedTasksByPriorityAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        TaskStatisticsResponseDto result = taskStatisticsService.getTaskStatistics(
                USER_ID,
                startDate,
                endDate);

        assertEquals(
                new BigDecimal("75.00"),
                result.getCompletionRate());
    }

    @Test
    void getTaskStatistics_shouldReturnNullCompletionRateWhenNoTaskIsPlanned() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(taskRepository.countPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(0L);

        when(taskRepository.countCompletedPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(0L);

        when(taskRepository.findCompletionDatesByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        when(taskRepository.countCompletedTasksByPriorityAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        TaskStatisticsResponseDto result = taskStatisticsService.getTaskStatistics(
                USER_ID,
                startDate,
                endDate);

        assertNull(result.getCompletionRate());
        assertNull(result.getDeadlineRespectRate());
    }

    @Test
    void getTaskStatistics_shouldCalculateDeadlineRespectRate() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(taskRepository.countPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(10L);

        when(taskRepository.countCompletedPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(4L);

        when(taskRepository.countCompletedOnTimeTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(3L);

        when(taskRepository.findCompletionDatesByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        when(taskRepository.countCompletedTasksByPriorityAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        TaskStatisticsResponseDto result = taskStatisticsService.getTaskStatistics(
                USER_ID,
                startDate,
                endDate);

        assertEquals(
                new BigDecimal("75.00"),
                result.getDeadlineRespectRate());
    }

    @Test
    void getTaskStatistics_shouldCalculateAverageCompletionTime() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(taskRepository.countPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(0L);

        when(taskRepository.countCompletedPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(0L);

        when(taskRepository.findCompletionDatesByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of(
                        new Object[] {
                                LocalDate.of(2026, 7, 1),
                                LocalDate.of(2026, 7, 3)
                        },
                        new Object[] {
                                LocalDate.of(2026, 7, 5),
                                LocalDate.of(2026, 7, 9)
                        },
                        new Object[] {
                                LocalDate.of(2026, 7, 10),
                                LocalDate.of(2026, 7, 16)
                        }));

        when(taskRepository.countCompletedTasksByPriorityAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        TaskStatisticsResponseDto result = taskStatisticsService.getTaskStatistics(
                USER_ID,
                startDate,
                endDate);

        assertEquals(
                new BigDecimal("4.00"),
                result.getAverageCompletionTimeDays());
    }

    @Test
    void getTaskStatistics_shouldReturnNullAverageCompletionTimeWhenNoTaskIsCompleted() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(taskRepository.countPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(0L);

        when(taskRepository.countCompletedPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(0L);

        when(taskRepository.findCompletionDatesByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        when(taskRepository.countCompletedTasksByPriorityAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        TaskStatisticsResponseDto result = taskStatisticsService.getTaskStatistics(
                USER_ID,
                startDate,
                endDate);

        assertNull(result.getAverageCompletionTimeDays());
    }

    @Test
    void getTaskStatistics_shouldCalculatePriorityBreakdown() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(taskRepository.countPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(0L);

        when(taskRepository.countCompletedPlannedTasksByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(0L);

        when(taskRepository.findCompletionDatesByPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        when(taskRepository.countCompletedTasksByPriorityAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of(
                        new Object[] {
                                TaskPriority.URGENT,
                                3L
                        },
                        new Object[] {
                                TaskPriority.IMPORTANT,
                                2L
                        },
                        new Object[] {
                                TaskPriority.NON_CRITICAL,
                                1L
                        }));

        TaskStatisticsResponseDto result = taskStatisticsService.getTaskStatistics(
                USER_ID,
                startDate,
                endDate);

        assertEquals(3, result.getPriorityBreakdown().size());

        assertEquals(
                TaskPriority.URGENT,
                result.getPriorityBreakdown().get(0).getPriority());

        assertEquals(
                3,
                result.getPriorityBreakdown().get(0).getCount());

        assertEquals(
                new BigDecimal("50.00"),
                result.getPriorityBreakdown().get(0).getPercentage());

        assertEquals(
                new BigDecimal("33.33"),
                result.getPriorityBreakdown().get(1).getPercentage());

        assertEquals(
                new BigDecimal("16.67"),
                result.getPriorityBreakdown().get(2).getPercentage());
    }

    @Test
    void getTaskStatistics_shouldUseTodayAsEffectiveEndDateForCurrentPeriod() {
        LocalDate startDate = LocalDate.of(2026, 8, 1);
        LocalDate endDate = LocalDate.of(2026, 8, 31);
        LocalDate today = LocalDate.of(2026, 8, 20);

        when(taskRepository.countPlannedTasksByPeriod(
                USER_ID,
                startDate,
                today))
                .thenReturn(0L);

        when(taskRepository.countCompletedPlannedTasksByPeriod(
                USER_ID,
                startDate,
                today))
                .thenReturn(0L);

        when(taskRepository.findCompletionDatesByPeriod(
                USER_ID,
                startDate,
                today))
                .thenReturn(List.of());

        when(taskRepository.countCompletedTasksByPriorityAndPeriod(
                USER_ID,
                startDate,
                today))
                .thenReturn(List.of());

        taskStatisticsService.getTaskStatistics(
                USER_ID,
                startDate,
                endDate);

        verify(taskRepository).countPlannedTasksByPeriod(
                USER_ID,
                startDate,
                today);

        verify(taskRepository).findCompletionDatesByPeriod(
                USER_ID,
                startDate,
                today);

        verify(taskRepository).countCompletedTasksByPriorityAndPeriod(
                USER_ID,
                startDate,
                today);
    }
}