package com.davexo.backend.service.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.response.statistics.ExpenseCategoryBreakdownResponseDto;
import com.davexo.backend.dto.response.statistics.ExpenseStatisticsResponseDto;
import com.davexo.backend.enums.StatisticsPeriod;
import com.davexo.backend.repository.ExpenseRepository;
import com.davexo.backend.util.StatisticsPeriodUtils;
import com.davexo.backend.util.StatisticsPeriodUtils.DateRange;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseStatisticsService {

    private static final int PERCENTAGE_CALCULATION_SCALE = 4;
    private static final int DISPLAY_SCALE = 2;

    private final ExpenseRepository expenseRepository;

    public ExpenseStatisticsResponseDto getExpenseStatistics(
            Integer userId,
            StatisticsPeriod periodType,
            LocalDate startDate,
            LocalDate endDate) {

        LocalDate effectiveEndDate = StatisticsPeriodUtils.getEffectiveEndDate(
                startDate,
                endDate);

        BigDecimal totalSpent = expenseRepository.sumAmountByUserAndPeriod(
                userId,
                startDate,
                effectiveEndDate);

        BigDecimal evolutionPercentage = calculateExpenseEvolution(
                userId,
                periodType,
                startDate,
                effectiveEndDate,
                totalSpent);

        BigDecimal historicalMonthlyAverage = calculateHistoricalMonthlyAverage(userId);

        List<ExpenseCategoryBreakdownResponseDto> categoryBreakdown = calculateExpenseCategoryBreakdown(
                userId,
                startDate,
                effectiveEndDate,
                totalSpent);

        return ExpenseStatisticsResponseDto.builder()
                .totalSpent(totalSpent)
                .evolutionPercentage(evolutionPercentage)
                .historicalMonthlyAverage(historicalMonthlyAverage)
                .categoryBreakdown(categoryBreakdown)
                .build();
    }

    private BigDecimal calculateExpenseEvolution(
            Integer userId,
            StatisticsPeriod periodType,
            LocalDate startDate,
            LocalDate effectiveEndDate,
            BigDecimal currentTotal) {

        DateRange previousPeriod = StatisticsPeriodUtils.getPreviousPeriod(
                periodType,
                startDate,
                effectiveEndDate);

        BigDecimal previousTotal = expenseRepository.sumAmountByUserAndPeriod(
                userId,
                previousPeriod.startDate(),
                previousPeriod.endDate());

        if (previousTotal.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return currentTotal
                .subtract(previousTotal)
                .divide(
                        previousTotal,
                        PERCENTAGE_CALCULATION_SCALE,
                        RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(
                        DISPLAY_SCALE,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal calculateHistoricalMonthlyAverage(
            Integer userId) {

        Optional<LocalDate> firstExpenseDate = expenseRepository.findFirstExpenseDateByUserId(
                userId);

        if (firstExpenseDate.isEmpty()) {
            return null;
        }

        YearMonth firstExpenseMonth = YearMonth.from(firstExpenseDate.get());

        YearMonth lastCompletedMonth = YearMonth.now().minusMonths(1);

        if (firstExpenseMonth.isAfter(lastCompletedMonth)) {
            return null;
        }

        long numberOfMonths = ChronoUnit.MONTHS.between(
                firstExpenseMonth,
                lastCompletedMonth) + 1;

        LocalDate historicalStartDate = firstExpenseMonth.atDay(1);

        LocalDate historicalEndDate = lastCompletedMonth.atEndOfMonth();

        BigDecimal totalHistoricalExpenses = expenseRepository.sumAmountByUserAndPeriod(
                userId,
                historicalStartDate,
                historicalEndDate);

        return totalHistoricalExpenses.divide(
                BigDecimal.valueOf(numberOfMonths),
                DISPLAY_SCALE,
                RoundingMode.HALF_UP);
    }

    private List<ExpenseCategoryBreakdownResponseDto> calculateExpenseCategoryBreakdown(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalSpent) {

        List<Object[]> categoryTotals = expenseRepository.sumAmountByCategoryAndPeriod(
                userId,
                startDate,
                endDate);

        return categoryTotals.stream()
                .map(row -> buildExpenseCategoryBreakdown(
                        row,
                        totalSpent))
                .toList();
    }

    private ExpenseCategoryBreakdownResponseDto buildExpenseCategoryBreakdown(
            Object[] row,
            BigDecimal totalSpent) {

        Integer categoryId = (Integer) row[0];
        String categoryName = (String) row[1];
        BigDecimal categoryTotal = (BigDecimal) row[2];

        BigDecimal percentage = calculatePercentage(
                categoryTotal,
                totalSpent);

        return ExpenseCategoryBreakdownResponseDto.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .totalAmount(categoryTotal)
                .percentage(percentage)
                .build();
    }

    private BigDecimal calculatePercentage(
            BigDecimal value,
            BigDecimal total) {

        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return value
                .divide(
                        total,
                        PERCENTAGE_CALCULATION_SCALE,
                        RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(
                        DISPLAY_SCALE,
                        RoundingMode.HALF_UP);
    }
}