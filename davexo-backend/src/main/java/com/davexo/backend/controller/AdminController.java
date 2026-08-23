package com.davexo.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.response.AdministrationStatsResponseDto;
import com.davexo.backend.exception.CustomErrorResponse;
import com.davexo.backend.service.AdministrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(
        name = "Administration",
        description = "Administration endpoints restricted to administrators")
public class AdminController {

    private final AdministrationService administrationService;

    @GetMapping(
            value = "/administration-stats",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get administration statistics",
            description = "Returns global administration statistics")
    @ApiResponse(
            responseCode = "200",
            description = "Administration statistics returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AdministrationStatsResponseDto.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "403",
            description = "Administrator role required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<AdministrationStatsResponseDto> getAdministrationStats() {

        AdministrationStatsResponseDto response =
                administrationService.getAdministrationStats();

        return ResponseEntity.ok(response);
    }
}