package com.davexo.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Global administration statistics")
public class AdministrationStatsResponseDto {

    @Schema(description = "Total number of registered users", requiredMode = Schema.RequiredMode.REQUIRED)
    private long totalUsers;
}