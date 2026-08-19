package com.davexo.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.davexo.backend.dto.request.BudgetRequestDto;
import com.davexo.backend.dto.response.BudgetResponseDto;
import com.davexo.backend.entity.Budget;

@Component
public class BudgetMapper {

    public BudgetResponseDto toBudgetResponseDto(Budget budget, List<Integer> followedCategoryIds) {

        return BudgetResponseDto.builder()
                .id(budget.getId())
                .name(budget.getName())
                .amount(budget.getAmount())
                .scope(budget.getScope())
                .followedCategoryIds(followedCategoryIds)
                .build();
    }
    
    public Budget toBudgetEntity(BudgetRequestDto budgetRequestDto) {
        return Budget.builder()
                .name(budgetRequestDto.getName())
                .amount(budgetRequestDto.getAmount())
                .build();
    }
}
