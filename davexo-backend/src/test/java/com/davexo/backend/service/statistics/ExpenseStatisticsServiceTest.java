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
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.davexo.backend.dto.response.statistics.ExpenseStatisticsResponseDto;
import com.davexo.backend.enums.StatisticsPeriod;
import com.davexo.backend.repository.ExpenseRepository;
import com.davexo.backend.util.StatisticsPeriodUtils;

@ExtendWith(MockitoExtension.class)
class ExpenseStatisticsServiceTest {

    private static final Integer USER_ID = 1;

    @Mock
    private ExpenseRepository expenseRepository;

    private ExpenseStatisticsService expenseStatisticsService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-08-20T12:00:00Z"),
                ZoneId.of("Europe/Paris"));

        StatisticsPeriodUtils statisticsPeriodUtils = new StatisticsPeriodUtils(fixedClock);

        expenseStatisticsService = new ExpenseStatisticsService(
                expenseRepository,
                statisticsPeriodUtils,
                fixedClock);
    }

    @Test
    void getExpenseStatistics_shouldCalculatePositiveEvolution() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(new BigDecimal("900.00"));

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)))
                .thenReturn(new BigDecimal("600.00"));

        when(expenseRepository.findFirstExpenseDateByUserId(USER_ID))
                .thenReturn(Optional.empty());

        when(expenseRepository.sumAmountByCategoryAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        ExpenseStatisticsResponseDto result = expenseStatisticsService.getExpenseStatistics(
                USER_ID,
                StatisticsPeriod.MONTH,
                startDate,
                endDate);

        assertEquals(
                new BigDecimal("900.00"),
                result.getTotalSpent());

        assertEquals(
                new BigDecimal("50.00"),
                result.getEvolutionPercentage());

        assertNull(result.getHistoricalMonthlyAverage());
    }

    @Test
    void getExpenseStatistics_shouldCalculateNegativeEvolution() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(new BigDecimal("600.00"));

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)))
                .thenReturn(new BigDecimal("800.00"));

        when(expenseRepository.findFirstExpenseDateByUserId(USER_ID))
                .thenReturn(Optional.empty());

        when(expenseRepository.sumAmountByCategoryAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        ExpenseStatisticsResponseDto result = expenseStatisticsService.getExpenseStatistics(
                USER_ID,
                StatisticsPeriod.MONTH,
                startDate,
                endDate);

        assertEquals(
                new BigDecimal("-25.00"),
                result.getEvolutionPercentage());
    }

    @Test
    void getExpenseStatistics_shouldReturnNullEvolutionWhenPreviousTotalIsZero() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(new BigDecimal("250.00"));

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)))
                .thenReturn(BigDecimal.ZERO);

        when(expenseRepository.findFirstExpenseDateByUserId(USER_ID))
                .thenReturn(Optional.empty());

        when(expenseRepository.sumAmountByCategoryAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        ExpenseStatisticsResponseDto result = expenseStatisticsService.getExpenseStatistics(
                USER_ID,
                StatisticsPeriod.MONTH,
                startDate,
                endDate);

        assertNull(result.getEvolutionPercentage());
    }

    @Test
    void getExpenseStatistics_shouldCalculateHistoricalMonthlyAverage() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(new BigDecimal("500.00"));

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)))
                .thenReturn(new BigDecimal("400.00"));

        when(expenseRepository.findFirstExpenseDateByUserId(USER_ID))
                .thenReturn(Optional.of(
                        LocalDate.of(2026, 5, 10)));

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 7, 31)))
                .thenReturn(new BigDecimal("1500.00"));

        when(expenseRepository.sumAmountByCategoryAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of());

        ExpenseStatisticsResponseDto result = expenseStatisticsService.getExpenseStatistics(
                USER_ID,
                StatisticsPeriod.MONTH,
                startDate,
                endDate);

        assertEquals(
                new BigDecimal("500.00"),
                result.getHistoricalMonthlyAverage());
    }

    @Test
    void getExpenseStatistics_shouldCalculateCategoryBreakdown() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(new BigDecimal("1000.00"));

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)))
                .thenReturn(new BigDecimal("1000.00"));

        when(expenseRepository.findFirstExpenseDateByUserId(USER_ID))
                .thenReturn(Optional.empty());

        when(expenseRepository.sumAmountByCategoryAndPeriod(
                USER_ID,
                startDate,
                endDate))
                .thenReturn(List.of(
                        new Object[] {
                                1,
                                "Logement",
                                new BigDecimal("700.00")
                        },
                        new Object[] {
                                2,
                                "Courses alimentaires",
                                new BigDecimal("300.00")
                        }));

        ExpenseStatisticsResponseDto result = expenseStatisticsService.getExpenseStatistics(
                USER_ID,
                StatisticsPeriod.MONTH,
                startDate,
                endDate);

        assertEquals(2, result.getCategoryBreakdown().size());

        assertEquals(
                "Logement",
                result.getCategoryBreakdown().get(0).getCategoryName());

        assertEquals(
                new BigDecimal("700.00"),
                result.getCategoryBreakdown().get(0).getTotalAmount());

        assertEquals(
                new BigDecimal("70.00"),
                result.getCategoryBreakdown().get(0).getPercentage());

        assertEquals(
                new BigDecimal("30.00"),
                result.getCategoryBreakdown().get(1).getPercentage());
    }

    @Test
    void getExpenseStatistics_shouldUseTodayAsEffectiveEndDateForCurrentMonth() {
        LocalDate startDate = LocalDate.of(2026, 8, 1);
        LocalDate endDate = LocalDate.of(2026, 8, 31);
        LocalDate today = LocalDate.of(2026, 8, 20);

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                startDate,
                today))
                .thenReturn(new BigDecimal("500.00"));

        when(expenseRepository.sumAmountByUserAndPeriod(
                USER_ID,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 20)))
                .thenReturn(new BigDecimal("400.00"));

        when(expenseRepository.findFirstExpenseDateByUserId(USER_ID))
                .thenReturn(Optional.empty());

        when(expenseRepository.sumAmountByCategoryAndPeriod(
                USER_ID,
                startDate,
                today))
                .thenReturn(List.of());

        expenseStatisticsService.getExpenseStatistics(
                USER_ID,
                StatisticsPeriod.MONTH,
                startDate,
                endDate);

        verify(expenseRepository).sumAmountByUserAndPeriod(
                USER_ID,
                startDate,
                today);

        verify(expenseRepository).sumAmountByCategoryAndPeriod(
                USER_ID,
                startDate,
                today);
    }
}