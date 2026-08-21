package com.davexo.backend.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.request.ExpenseRequestDto;
import com.davexo.backend.dto.response.ExpenseResponseDto;
import com.davexo.backend.entity.Expense;
import com.davexo.backend.entity.ExpenseCategory;
import com.davexo.backend.entity.User;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.ExpenseMapper;
import com.davexo.backend.repository.ExpenseCategoryRepository;
import com.davexo.backend.repository.ExpenseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseMapper expenseMapper;
    private final Clock clock;

    @Transactional
    public ExpenseResponseDto addExpense(
            ExpenseRequestDto expenseRequestDto,
            User authenticatedUser) {

        Expense expense = expenseMapper.toExpenseEntity(expenseRequestDto);

        expense.setUser(authenticatedUser);

        expense.setExpenseCategory(
                expenseCategoryRepository.findByIdAndUserId(
                        expenseRequestDto.getExpenseCategoryId(),
                        authenticatedUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category Not found")));

        if (expenseRequestDto.getExpenseDate() == null) {
            expense.setExpenseDate(LocalDate.now(clock));
        }

        Expense savedExpense = expenseRepository.save(expense);

        return expenseMapper.toExpenseResponseDto(savedExpense);
    }

    @Transactional
    public ExpenseResponseDto updateExpense(
            ExpenseRequestDto expenseRequestDto,
            Integer expenseId,
            Integer userId) {

        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (expenseRequestDto.getExpenseDate() != null) {
            expense.setExpenseDate(expenseRequestDto.getExpenseDate());
        }

        ExpenseCategory expenseCategory = expenseCategoryRepository
                .findByIdAndUserId(
                        expenseRequestDto.getExpenseCategoryId(),
                        userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        expense.setLabel(expenseRequestDto.getLabel());
        expense.setAmount(expenseRequestDto.getAmount());
        expense.setNote(expenseRequestDto.getNote());
        expense.setExpenseCategory(expenseCategory);

        Expense savedExpense = expenseRepository.save(expense);

        return expenseMapper.toExpenseResponseDto(savedExpense);
    }

    @Transactional
    public void deleteExpense(
            Integer expenseId,
            Integer userId) {

        long deleteCount = expenseRepository.deleteByIdAndUserId(
                expenseId,
                userId);

        if (deleteCount == 0) {
            throw new ResourceNotFoundException("Expense not found");
        }
    }

    public ExpenseResponseDto getExpenseDetail(
            Integer expenseId,
            Integer userId) {

        Expense expense = expenseRepository.findByIdAndUserId(
                expenseId,
                userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        return expenseMapper.toExpenseResponseDto(expense);
    }

    public List<ExpenseResponseDto> getAllExpenses(Integer userId) {

        List<Expense> expenseList = expenseRepository.findAllByUserId(userId);

        return expenseList.stream()
                .map(expenseMapper::toExpenseResponseDto)
                .toList();
    }
}