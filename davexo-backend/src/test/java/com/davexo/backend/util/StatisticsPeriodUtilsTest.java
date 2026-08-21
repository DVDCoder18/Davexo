package com.davexo.backend.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.davexo.backend.enums.StatisticsPeriod;
import com.davexo.backend.util.StatisticsPeriodUtils.DateRange;

class StatisticsPeriodUtilsTest {

    private StatisticsPeriodUtils statisticsPeriodUtils;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-08-20T12:00:00Z"),
                ZoneId.of("Europe/Paris"));

        statisticsPeriodUtils = new StatisticsPeriodUtils(fixedClock);
    }

    @Test
    void validatePeriod_shouldAcceptValidDayPeriod() {
        LocalDate date = LocalDate.of(2026, 8, 19);

        assertDoesNotThrow(() -> statisticsPeriodUtils.validatePeriod(
                StatisticsPeriod.DAY,
                date,
                date));
    }

    @Test
    void validatePeriod_shouldRejectDayPeriodWithDifferentDates() {
        LocalDate startDate = LocalDate.of(2026, 8, 18);
        LocalDate endDate = LocalDate.of(2026, 8, 19);

        assertThrows(
                IllegalArgumentException.class,
                () -> statisticsPeriodUtils.validatePeriod(
                        StatisticsPeriod.DAY,
                        startDate,
                        endDate));
    }

    @Test
    void validatePeriod_shouldAcceptValidWeekPeriod() {
        LocalDate startDate = LocalDate.of(2026, 8, 10);
        LocalDate endDate = LocalDate.of(2026, 8, 16);

        assertDoesNotThrow(() -> statisticsPeriodUtils.validatePeriod(
                StatisticsPeriod.WEEK,
                startDate,
                endDate));
    }

    @Test
    void validatePeriod_shouldRejectWeekNotStartingOnMonday() {
        LocalDate startDate = LocalDate.of(2026, 8, 11);
        LocalDate endDate = LocalDate.of(2026, 8, 17);

        assertThrows(
                IllegalArgumentException.class,
                () -> statisticsPeriodUtils.validatePeriod(
                        StatisticsPeriod.WEEK,
                        startDate,
                        endDate));
    }

    @Test
    void validatePeriod_shouldAcceptValidMonthPeriod() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        assertDoesNotThrow(() -> statisticsPeriodUtils.validatePeriod(
                StatisticsPeriod.MONTH,
                startDate,
                endDate));
    }

    @Test
    void validatePeriod_shouldRejectIncompleteMonthPeriod() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 30);

        assertThrows(
                IllegalArgumentException.class,
                () -> statisticsPeriodUtils.validatePeriod(
                        StatisticsPeriod.MONTH,
                        startDate,
                        endDate));
    }

    @Test
    void validatePeriod_shouldAcceptValidYearPeriod() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        assertDoesNotThrow(() -> statisticsPeriodUtils.validatePeriod(
                StatisticsPeriod.YEAR,
                startDate,
                endDate));
    }

    @Test
    void validatePeriod_shouldRejectFuturePeriod() {
        LocalDate futureDate = LocalDate.of(2026, 8, 21);

        assertThrows(
                IllegalArgumentException.class,
                () -> statisticsPeriodUtils.validatePeriod(
                        StatisticsPeriod.DAY,
                        futureDate,
                        futureDate));
    }

    @Test
    void getEffectiveEndDate_shouldReturnTodayForOngoingPeriod() {
        LocalDate startDate = LocalDate.of(2026, 8, 1);
        LocalDate endDate = LocalDate.of(2026, 8, 31);

        LocalDate result = statisticsPeriodUtils.getEffectiveEndDate(
                startDate,
                endDate);

        assertEquals(
                LocalDate.of(2026, 8, 20),
                result);
    }

    @Test
    void getPreviousPeriod_shouldReturnPreviousDay() {
        LocalDate date = LocalDate.of(2026, 8, 20);

        DateRange result = statisticsPeriodUtils.getPreviousPeriod(
                StatisticsPeriod.DAY,
                date,
                date);

        assertEquals(
                LocalDate.of(2026, 8, 19),
                result.startDate());

        assertEquals(
                LocalDate.of(2026, 8, 19),
                result.endDate());
    }

    @Test
    void getPreviousPeriod_shouldKeepElapsedDaysForWeek() {
        LocalDate startDate = LocalDate.of(2026, 8, 17);
        LocalDate effectiveEndDate = LocalDate.of(2026, 8, 20);

        DateRange result = statisticsPeriodUtils.getPreviousPeriod(
                StatisticsPeriod.WEEK,
                startDate,
                effectiveEndDate);

        assertEquals(
                LocalDate.of(2026, 8, 10),
                result.startDate());

        assertEquals(
                LocalDate.of(2026, 8, 13),
                result.endDate());
    }

    @Test
    void getPreviousPeriod_shouldLimitPreviousMonthToItsLastDay() {
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate effectiveEndDate = LocalDate.of(2026, 3, 31);

        DateRange result = statisticsPeriodUtils.getPreviousPeriod(
                StatisticsPeriod.MONTH,
                startDate,
                effectiveEndDate);

        assertEquals(
                LocalDate.of(2026, 2, 1),
                result.startDate());

        assertEquals(
                LocalDate.of(2026, 2, 28),
                result.endDate());
    }
}