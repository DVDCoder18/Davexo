package com.davexo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.response.DashboardResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboard(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        DashboardResponseDto response = dashboardService.getDashboard(
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
}