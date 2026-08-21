package com.davexo.backend.util;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.davexo.backend.enums.StatisticsPeriod;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StatisticsPeriodUtils {

    private final Clock clock;

    public void validatePeriod(
            StatisticsPeriod periodType,
            LocalDate startDate,
            LocalDate endDate) {

        if (periodType == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Period type, start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        if (startDate.isAfter(LocalDate.now(clock))) {
            throw new IllegalArgumentException(
                    "Statistics cannot be requested for a future period");
        }

        switch (periodType) {
            case DAY -> validateDayPeriod(startDate, endDate);
            case WEEK -> validateWeekPeriod(startDate, endDate);
            case MONTH -> validateMonthPeriod(startDate, endDate);
            case YEAR -> validateYearPeriod(startDate, endDate);
        }
    }

    public LocalDate getEffectiveEndDate(
            LocalDate startDate,
            LocalDate endDate) {

        LocalDate today = LocalDate.now(clock);

        if (!startDate.isAfter(today) && endDate.isAfter(today)) {
            return today;
        }

        return endDate;
    }

    public DateRange getPreviousPeriod(
            StatisticsPeriod periodType,
            LocalDate startDate,
            LocalDate effectiveEndDate) {

        return switch (periodType) {
            case DAY -> getPreviousDayPeriod(startDate);
            case WEEK -> getPreviousWeekPeriod(startDate, effectiveEndDate);
            case MONTH -> getPreviousMonthPeriod(startDate, effectiveEndDate);
            case YEAR -> getPreviousYearPeriod(startDate, effectiveEndDate);
        };
    }

    private void validateDayPeriod(
            LocalDate startDate,
            LocalDate endDate) {

        if (!startDate.equals(endDate)) {
            throw new IllegalArgumentException(
                    "For DAY period, start date and end date must be identical");
        }
    }

    private void validateWeekPeriod(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalArgumentException(
                    "A WEEK period must start on Monday");
        }

        LocalDate expectedEndDate = startDate.plusDays(6);

        if (!endDate.equals(expectedEndDate)) {
            throw new IllegalArgumentException(
                    "A WEEK period must end on Sunday of the same week");
        }
    }

    private void validateMonthPeriod(
            LocalDate startDate,
            LocalDate endDate) {

        YearMonth selectedMonth = YearMonth.from(startDate);

        LocalDate expectedStartDate = selectedMonth.atDay(1);
        LocalDate expectedEndDate = selectedMonth.atEndOfMonth();

        if (!startDate.equals(expectedStartDate)) {
            throw new IllegalArgumentException(
                    "A MONTH period must start on the first day of the month");
        }

        if (!endDate.equals(expectedEndDate)) {
            throw new IllegalArgumentException(
                    "A MONTH period must end on the last day of the month");
        }
    }

    private void validateYearPeriod(
            LocalDate startDate,
            LocalDate endDate) {

        LocalDate expectedStartDate = LocalDate.of(startDate.getYear(), 1, 1);
        LocalDate expectedEndDate = LocalDate.of(startDate.getYear(), 12, 31);

        if (!startDate.equals(expectedStartDate)) {
            throw new IllegalArgumentException(
                    "A YEAR period must start on January 1st");
        }

        if (!endDate.equals(expectedEndDate)) {
            throw new IllegalArgumentException(
                    "A YEAR period must end on December 31st of the same year");
        }
    }

    private DateRange getPreviousDayPeriod(LocalDate startDate) {
        LocalDate previousDay = startDate.minusDays(1);

        return new DateRange(previousDay, previousDay);
    }

    private DateRange getPreviousWeekPeriod(
            LocalDate startDate,
            LocalDate effectiveEndDate) {

        LocalDate previousStartDate = startDate.minusWeeks(1);

        long elapsedDays = ChronoUnit.DAYS.between(
                startDate,
                effectiveEndDate);

        LocalDate previousEndDate = previousStartDate.plusDays(elapsedDays);

        return new DateRange(previousStartDate, previousEndDate);
    }

    private DateRange getPreviousMonthPeriod(
            LocalDate startDate,
            LocalDate effectiveEndDate) {

        YearMonth previousMonth = YearMonth.from(startDate).minusMonths(1);

        LocalDate previousStartDate = previousMonth.atDay(1);

        int comparableDay = Math.min(
                effectiveEndDate.getDayOfMonth(),
                previousMonth.lengthOfMonth());

        LocalDate previousEndDate = previousMonth.atDay(comparableDay);

        return new DateRange(previousStartDate, previousEndDate);
    }

    private DateRange getPreviousYearPeriod(
            LocalDate startDate,
            LocalDate effectiveEndDate) {

        LocalDate previousStartDate = LocalDate.of(
                startDate.getYear() - 1,
                1,
                1);

        LocalDate previousEndDate = effectiveEndDate.minusYears(1);

        return new DateRange(previousStartDate, previousEndDate);
    }

    public record DateRange(
            LocalDate startDate,
            LocalDate endDate) {
    }
}