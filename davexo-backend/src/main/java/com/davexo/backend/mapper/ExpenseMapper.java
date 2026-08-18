package com.davexo.backend.mapper;

import org.springframework.stereotype.Component;

import com.davexo.backend.dto.request.ExpenseRequestDto;
import com.davexo.backend.dto.response.ExpenseResponseDto;
import com.davexo.backend.entity.Expense;

@Component
public class ExpenseMapper {

    public ExpenseResponseDto toExpenseResponseDto(Expense expense) {
        return ExpenseResponseDto.builder()
                .id(expense.getId())
                .label(expense.getLabel())
                .amount(expense.getAmount())
                .note(expense.getNote())
                .expenseDate(expense.getExpenseDate())
                .expenseCategoryId(expense.getExpenseCategory().getId())
                .expenseCategoryName(expense.getExpenseCategory().getName())
                .build();

    }

    public Expense toExpenseEntity(ExpenseRequestDto expenseRequestDto) {
        
        return Expense.builder()
            .label(expenseRequestDto.getLabel())
            .amount(expenseRequestDto.getAmount())
            .note(expenseRequestDto.getNote())
            .expenseDate(expenseRequestDto.getExpenseDate())
            .build();
    }

}
