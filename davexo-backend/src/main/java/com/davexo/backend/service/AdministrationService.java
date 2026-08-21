package com.davexo.backend.service;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.response.AdministrationStatsResponseDto;
import com.davexo.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdministrationService {

    private final UserRepository userRepository;

    public AdministrationStatsResponseDto getAdministrationStats() {

        long totalUsers = userRepository.count();

        return AdministrationStatsResponseDto.builder()
                .totalUsers(totalUsers)
                .build();
    }
}