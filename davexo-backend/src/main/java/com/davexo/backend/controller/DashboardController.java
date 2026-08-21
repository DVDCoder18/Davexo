package com.davexo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.response.DashboardResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(
    name = "Dashboard",
    description = "Dashboard data for the authenticated user")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(
            summary = "Get user dashboard",
            description = "Returns dashboard KPIs, priority tasks, shopping items, recent expenses and global budget consumption")
    @ApiResponse(
            responseCode = "200",
            description = "Dashboard returned successfully")
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required")
    public ResponseEntity<DashboardResponseDto> getDashboard(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        DashboardResponseDto response = dashboardService.getDashboard(
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
}