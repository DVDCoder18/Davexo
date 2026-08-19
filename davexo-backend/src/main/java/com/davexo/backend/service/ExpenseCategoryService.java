package com.davexo.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.request.ExpenseCategoryRequestDto;
import com.davexo.backend.dto.response.ExpenseCategoryResponseDto;
import com.davexo.backend.entity.Budget;
import com.davexo.backend.entity.ExpenseCategory;
import com.davexo.backend.entity.User;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.ExpenseCategoryMapper;
import com.davexo.backend.repository.BudgetRepository;
import com.davexo.backend.repository.ExpenseCategoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseCategoryMapper expenseCategoryMapper;


    @Transactional
    public ExpenseCategoryResponseDto addExpenseCategory(ExpenseCategoryRequestDto expenseCategoryRequestDto,
            User authenticatedUser) {

        Budget budget = null;

        if (expenseCategoryRepository.existsByNameIgnoreCaseAndUserId(expenseCategoryRequestDto.getName(),
                authenticatedUser.getId())) {
            throw new BusinessException("You already possess an expense category with this name");
        }

        if (expenseCategoryRequestDto.getBudgetId() != null) {
            budget = budgetRepository.findByIdAndUserId(
                    expenseCategoryRequestDto.getBudgetId(),
                    authenticatedUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        }

        ExpenseCategory expenseCategory = expenseCategoryMapper.toExpenseCategoryEntity(expenseCategoryRequestDto);

        expenseCategory.setUser(authenticatedUser);
        expenseCategory.setBudget(budget);

        ExpenseCategory savedExpenseCategory = expenseCategoryRepository.save(expenseCategory);

        return expenseCategoryMapper.toExpenseCategoryResponseDto(savedExpenseCategory);
    }
    

    @Transactional
    public ExpenseCategoryResponseDto updateExpenseCategory(ExpenseCategoryRequestDto expenseCategoryRequestDto,
            Integer expenseCategoryId, Integer userId) {

        ExpenseCategory expenseCategory = expenseCategoryRepository.findByIdAndUserId(expenseCategoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found"));

        if (expenseCategoryRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(expenseCategoryRequestDto.getName(),
                userId, expenseCategoryId)) {
            throw new BusinessException("You already possess an expense category with this name");
        }

        if (expenseCategoryRequestDto.getBudgetId() != null) {
            Budget budget = budgetRepository.findByIdAndUserId(expenseCategoryRequestDto.getBudgetId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

            expenseCategory.setBudget(budget);
        } else {
            expenseCategory.setBudget(null);
        }

        expenseCategory.setName(expenseCategoryRequestDto.getName());

        ExpenseCategory savedExpenseCategory = expenseCategoryRepository.save(expenseCategory);

        return expenseCategoryMapper.toExpenseCategoryResponseDto(savedExpenseCategory);

    }

    @Transactional 
    public void deleteExpenseCategory(Integer expenseCategoryId, Integer userId) {
        long deleteCount = expenseCategoryRepository.deleteByIdAndUserId(expenseCategoryId, userId);

        if (deleteCount == 0) {
            throw new ResourceNotFoundException("Expense category not found");
        }
    }

    @Transactional
    public ExpenseCategoryResponseDto getExpenseCategoryDetail(Integer expenseCategoryId, Integer userId) {

        ExpenseCategory expenseCategory = expenseCategoryRepository.findByIdAndUserId(expenseCategoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found"));

        return expenseCategoryMapper.toExpenseCategoryResponseDto(expenseCategory);
    }
    
    @Transactional
    public List<ExpenseCategoryResponseDto> getAllExpenseCategories(Integer userId) {
        
        List<ExpenseCategory> expenseCategoryList = expenseCategoryRepository.findAllByUserId(userId);

        return expenseCategoryList.stream()
            .map(expenseCategoryMapper::toExpenseCategoryResponseDto)
                .toList();
    }
    
}
