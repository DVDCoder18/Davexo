package com.davexo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.response.AdministrationStatsResponseDto;
import com.davexo.backend.service.AdministrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Administration endpoints restricted to administrators")
public class AdminController {

    private final AdministrationService administrationService;

    @GetMapping("/administration-stats")
    @Operation(summary = "Get administration statistics", description = "Returns global administration statistics")
    @ApiResponse(responseCode = "200", description = "Administration statistics returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Administrator role required")
    public ResponseEntity<AdministrationStatsResponseDto> getAdministrationStats() {

        AdministrationStatsResponseDto response = administrationService.getAdministrationStats();

        return ResponseEntity.ok(response);
    }
}