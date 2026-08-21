package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryServiceTest {

    private static final Integer USER_ID = 1;
    private static final Integer CATEGORY_ID = 10;
    private static final Integer BUDGET_ID = 20;

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseCategoryMapper expenseCategoryMapper;

    private ExpenseCategoryService expenseCategoryService;

    @BeforeEach
    void setUp() {
        expenseCategoryService = new ExpenseCategoryService(
                expenseCategoryRepository,
                budgetRepository,
                expenseCategoryMapper);
    }

    @Test
    void addExpenseCategory_shouldRejectDuplicateNameForSameUser() {
        ExpenseCategoryRequestDto dto = createRequestDto();
        User user = createUser();

        when(expenseCategoryRepository.existsByNameIgnoreCaseAndUserId(
                dto.getName(),
                USER_ID))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> expenseCategoryService.addExpenseCategory(
                        dto,
                        user));

        assertEquals(
                "You already possess an expense category with this name",
                exception.getMessage());

        verify(expenseCategoryRepository, never())
                .save(any(ExpenseCategory.class));
    }

    @Test
    void addExpenseCategory_shouldRejectBudgetNotOwnedByUser() {
        ExpenseCategoryRequestDto dto = createRequestDto();
        dto.setBudgetId(BUDGET_ID);

        User user = createUser();

        when(expenseCategoryRepository.existsByNameIgnoreCaseAndUserId(
                dto.getName(),
                USER_ID))
                .thenReturn(false);

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseCategoryService.addExpenseCategory(
                        dto,
                        user));

        assertEquals(
                "Budget not found",
                exception.getMessage());

        verify(expenseCategoryRepository, never())
                .save(any(ExpenseCategory.class));
    }

    @Test
    void addExpenseCategory_shouldLinkCategoryToSelectedBudget() {
        ExpenseCategoryRequestDto dto = createRequestDto();
        dto.setBudgetId(BUDGET_ID);

        User user = createUser();

        Budget budget = new Budget();
        ExpenseCategory category = new ExpenseCategory();
        ExpenseCategoryResponseDto responseDto = new ExpenseCategoryResponseDto();

        when(expenseCategoryRepository.existsByNameIgnoreCaseAndUserId(
                dto.getName(),
                USER_ID))
                .thenReturn(false);

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.of(budget));

        when(expenseCategoryMapper.toExpenseCategoryEntity(dto))
                .thenReturn(category);

        when(expenseCategoryRepository.save(category))
                .thenReturn(category);

        when(expenseCategoryMapper.toExpenseCategoryResponseDto(category))
                .thenReturn(responseDto);

        expenseCategoryService.addExpenseCategory(
                dto,
                user);

        assertSame(
                user,
                category.getUser());

        assertSame(
                budget,
                category.getBudget());
    }

    @Test
    void addExpenseCategory_shouldAllowCategoryWithoutBudget() {
        ExpenseCategoryRequestDto dto = createRequestDto();
        dto.setBudgetId(null);

        User user = createUser();
        ExpenseCategory category = new ExpenseCategory();

        when(expenseCategoryRepository.existsByNameIgnoreCaseAndUserId(
                dto.getName(),
                USER_ID))
                .thenReturn(false);

        when(expenseCategoryMapper.toExpenseCategoryEntity(dto))
                .thenReturn(category);

        when(expenseCategoryRepository.save(category))
                .thenReturn(category);

        when(expenseCategoryMapper.toExpenseCategoryResponseDto(category))
                .thenReturn(new ExpenseCategoryResponseDto());

        expenseCategoryService.addExpenseCategory(
                dto,
                user);

        assertNull(category.getBudget());

        verify(budgetRepository, never())
                .findByIdAndUserId(any(), any());
    }

    @Test
    void updateExpenseCategory_shouldRejectDuplicateNameFromAnotherCategory() {
        ExpenseCategoryRequestDto dto = createRequestDto();
        ExpenseCategory category = new ExpenseCategory();

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(expenseCategoryRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(
                dto.getName(),
                USER_ID,
                CATEGORY_ID))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> expenseCategoryService.updateExpenseCategory(
                        dto,
                        CATEGORY_ID,
                        USER_ID));

        assertEquals(
                "You already possess an expense category with this name",
                exception.getMessage());

        verify(expenseCategoryRepository, never())
                .save(any(ExpenseCategory.class));
    }

    @Test
    void updateExpenseCategory_shouldReplaceBudget() {
        ExpenseCategory category = new ExpenseCategory();

        Budget oldBudget = new Budget();
        oldBudget.setId(5);
        category.setBudget(oldBudget);

        Budget newBudget = new Budget();
        newBudget.setId(BUDGET_ID);

        ExpenseCategoryRequestDto dto = createRequestDto();
        dto.setBudgetId(BUDGET_ID);

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(expenseCategoryRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(
                dto.getName(),
                USER_ID,
                CATEGORY_ID))
                .thenReturn(false);

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.of(newBudget));

        when(expenseCategoryRepository.save(category))
                .thenReturn(category);

        when(expenseCategoryMapper.toExpenseCategoryResponseDto(category))
                .thenReturn(new ExpenseCategoryResponseDto());

        expenseCategoryService.updateExpenseCategory(
                dto,
                CATEGORY_ID,
                USER_ID);

        assertSame(
                newBudget,
                category.getBudget());
    }

    @Test
    void updateExpenseCategory_shouldRemoveBudgetWhenBudgetIdIsNull() {
        ExpenseCategory category = new ExpenseCategory();

        Budget existingBudget = new Budget();
        existingBudget.setId(BUDGET_ID);
        category.setBudget(existingBudget);

        ExpenseCategoryRequestDto dto = createRequestDto();
        dto.setBudgetId(null);

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(expenseCategoryRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(
                dto.getName(),
                USER_ID,
                CATEGORY_ID))
                .thenReturn(false);

        when(expenseCategoryRepository.save(category))
                .thenReturn(category);

        when(expenseCategoryMapper.toExpenseCategoryResponseDto(category))
                .thenReturn(new ExpenseCategoryResponseDto());

        expenseCategoryService.updateExpenseCategory(
                dto,
                CATEGORY_ID,
                USER_ID);

        assertNull(category.getBudget());
    }

    @Test
    void deleteExpenseCategory_shouldThrowWhenCategoryIsNotFound() {
        when(expenseCategoryRepository.deleteByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(0L);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseCategoryService.deleteExpenseCategory(
                        CATEGORY_ID,
                        USER_ID));

        assertEquals(
                "Expense category not found",
                exception.getMessage());
    }

    private User createUser() {
        User user = new User();
        user.setId(USER_ID);

        return user;
    }

    private ExpenseCategoryRequestDto createRequestDto() {
        ExpenseCategoryRequestDto dto = new ExpenseCategoryRequestDto();

        dto.setName("Food");
        dto.setBudgetId(null);

        return dto;
    }
}