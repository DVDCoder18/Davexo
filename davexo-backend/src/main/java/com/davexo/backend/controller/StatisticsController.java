package com.davexo.backend.controller;

import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.response.statistics.StatisticsResponseDto;
import com.davexo.backend.enums.StatisticsPeriod;
import com.davexo.backend.exception.CustomErrorResponse;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.statistics.StatisticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(
        name = "Statistics",
        description = "Expense and task statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get statistics",
            description = "Returns expense and task statistics for the requested period")
    @ApiResponse(
            responseCode = "200",
            description = "Statistics returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            implementation = StatisticsResponseDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid period or date parameters",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<StatisticsResponseDto> getStatistics(
            @Parameter(description = "Statistics period type")
            @RequestParam("periodType") StatisticsPeriod periodType,

            @Parameter(description = "Start date of the requested period")
            @RequestParam("startDate") LocalDate startDate,

            @Parameter(description = "End date of the requested period")
            @RequestParam("endDate") LocalDate endDate,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        StatisticsResponseDto statistics =
                statisticsService.getStatistics(
                        customUserDetails.getUser().getId(),
                        periodType,
                        startDate,
                        endDate);

        return ResponseEntity.ok(statistics);
    }
}