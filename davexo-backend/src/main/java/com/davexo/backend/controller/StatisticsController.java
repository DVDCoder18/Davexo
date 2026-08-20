package com.davexo.backend.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.response.statistics.StatisticsResponseDto;
import com.davexo.backend.enums.StatisticsPeriod;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.statistics.StatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsResponseDto> getStatistics(
            @RequestParam StatisticsPeriod periodType,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        StatisticsResponseDto statistics = statisticsService.getStatistics(
                customUserDetails.getUser().getId(),
                periodType,
                startDate,
                endDate);

        return ResponseEntity.ok(statistics);
    }
}