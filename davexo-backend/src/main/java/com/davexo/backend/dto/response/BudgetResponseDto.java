package com.davexo.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.davexo.backend.enums.BudgetScope;

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
public class BudgetResponseDto {

    private Integer id;

    private String name;
    
    private BigDecimal amount;

    private BudgetScope scope;

    private List<Integer> followedCategoryIds;
}
