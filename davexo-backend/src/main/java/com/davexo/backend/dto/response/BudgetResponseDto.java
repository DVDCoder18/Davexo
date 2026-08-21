package com.davexo.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.davexo.backend.enums.BudgetScope;

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
@Schema(description = "Aggregated dashboard data for the authenticated user")
public class BudgetResponseDto {

    private Integer id;

    private String name;
    
    private BigDecimal amount;

    private BudgetScope scope;

    private List<Integer> followedCategoryIds;
}
