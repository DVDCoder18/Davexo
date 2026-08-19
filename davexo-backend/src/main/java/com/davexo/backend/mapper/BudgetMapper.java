package com.davexo.backend.mapper;

import org.springframework.stereotype.Component;

import com.davexo.backend.dto.request.BudgetRequestDto;
import com.davexo.backend.dto.response.BudgetResponseDto;
import com.davexo.backend.entity.Budget;

@Component
public class BudgetMapper {

    public BudgetResponseDto toBudgetResponseDto(Budget budget) {

        return BudgetResponseDto.builder()
                .id(budget.getId())
                .name(budget.getName())
                .amount(budget.getAmount())
                .scope(budget.getScope())
                .build();
    }
    
    public Budget toBudgetEntity(BudgetRequestDto budgetRequestDto) {
        return Budget.builder()
                .name(budgetRequestDto.getName())
                .amount(budgetRequestDto.getAmount())
                .scope(budgetRequestDto.getScope())
                .build();
    }
}
