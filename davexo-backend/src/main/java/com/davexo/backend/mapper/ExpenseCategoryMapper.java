package com.davexo.backend.mapper;

import org.springframework.stereotype.Component;

import com.davexo.backend.dto.request.ExpenseCategoryRequestDto;
import com.davexo.backend.dto.response.ExpenseCategoryResponseDto;
import com.davexo.backend.entity.ExpenseCategory;

@Component
public class ExpenseCategoryMapper {

    public ExpenseCategoryResponseDto toExpenseCategoryResponseDto(ExpenseCategory expenseCategory) {
        return ExpenseCategoryResponseDto.builder()
                .id(expenseCategory.getId())
                .name(expenseCategory.getName())
                .budgetId(
                        expenseCategory.getBudget() != null
                                ? expenseCategory.getBudget().getId()
                                : null)
                .budgetName(
                        expenseCategory.getBudget() != null
                                ? expenseCategory.getBudget().getName()
                                : null)
                .build();
    }

    public ExpenseCategory toExpenseCategoryEntity(ExpenseCategoryRequestDto expenseCategoryRequestDto) {
        return ExpenseCategory.builder()
            .name(expenseCategoryRequestDto.getName())
            .build();
    }
}
