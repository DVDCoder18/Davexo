package com.davexo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.response.AdministrationStatsResponseDto;
import com.davexo.backend.service.AdministrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdministrationService administrationService;

    @GetMapping("/administration-stats")
    public ResponseEntity<AdministrationStatsResponseDto> getAdministrationStats() {

        AdministrationStatsResponseDto response = administrationService.getAdministrationStats();

        return ResponseEntity.ok(response);
    }
}