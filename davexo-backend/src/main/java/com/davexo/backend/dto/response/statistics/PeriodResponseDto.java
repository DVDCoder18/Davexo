package com.davexo.backend.dto.response.statistics;

import java.time.LocalDate;

import com.davexo.backend.enums.StatisticsPeriod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Effective period used to calculate statistics")
public class PeriodResponseDto {

    @NotNull
    @Schema(description = "Statistics period type", allowableValues = { "DAY", "WEEK", "MONTH", "YEAR" })
    private StatisticsPeriod periodType;

    @NotNull
    @Schema(description = "First date included in the statistics period")
    private LocalDate startDate;

    @NotNull
    @Schema(description = "Last date included in the statistics period")
    private LocalDate endDate;
}