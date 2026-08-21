package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.davexo.backend.dto.request.ExpenseRequestDto;
import com.davexo.backend.dto.response.ExpenseResponseDto;
import com.davexo.backend.entity.Expense;
import com.davexo.backend.entity.ExpenseCategory;
import com.davexo.backend.entity.User;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.ExpenseMapper;
import com.davexo.backend.repository.ExpenseCategoryRepository;
import com.davexo.backend.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    private static final Integer USER_ID = 1;
    private static final Integer EXPENSE_ID = 10;
    private static final Integer CATEGORY_ID = 5;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-08-20T12:00:00Z"),
                ZoneId.of("Europe/Paris"));

        expenseService = new ExpenseService(
                expenseRepository,
                expenseCategoryRepository,
                expenseMapper,
                fixedClock);
    }

    @Test
    void addExpense_shouldAssociateUserCategoryAndSaveExpense() {
        ExpenseRequestDto dto = createExpenseRequestDto();
        User authenticatedUser = createUser();

        Expense mappedExpense = new Expense();
        ExpenseCategory expenseCategory = new ExpenseCategory();
        Expense savedExpense = new Expense();
        ExpenseResponseDto responseDto = new ExpenseResponseDto();

        when(expenseMapper.toExpenseEntity(dto))
                .thenReturn(mappedExpense);

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(expenseCategory));

        when(expenseRepository.save(mappedExpense))
                .thenReturn(savedExpense);

        when(expenseMapper.toExpenseResponseDto(savedExpense))
                .thenReturn(responseDto);

        ExpenseResponseDto result = expenseService.addExpense(
                dto,
                authenticatedUser);

        assertSame(
                authenticatedUser,
                mappedExpense.getUser());

        assertSame(
                expenseCategory,
                mappedExpense.getExpenseCategory());

        assertSame(
                responseDto,
                result);

        verify(expenseRepository).save(mappedExpense);
    }

    @Test
    void addExpense_shouldUseTodayWhenExpenseDateIsNull() {
        ExpenseRequestDto dto = createExpenseRequestDto();
        dto.setExpenseDate(null);

        User authenticatedUser = createUser();

        Expense mappedExpense = new Expense();
        ExpenseCategory expenseCategory = new ExpenseCategory();

        when(expenseMapper.toExpenseEntity(dto))
                .thenReturn(mappedExpense);

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(expenseCategory));

        when(expenseRepository.save(mappedExpense))
                .thenReturn(mappedExpense);

        when(expenseMapper.toExpenseResponseDto(mappedExpense))
                .thenReturn(new ExpenseResponseDto());

        expenseService.addExpense(
                dto,
                authenticatedUser);

        assertEquals(
                LocalDate.of(2026, 8, 20),
                mappedExpense.getExpenseDate());
    }

    @Test
    void addExpense_shouldKeepProvidedExpenseDate() {
        ExpenseRequestDto dto = createExpenseRequestDto();

        LocalDate expenseDate = LocalDate.of(
                2026,
                8,
                15);

        dto.setExpenseDate(expenseDate);

        User authenticatedUser = createUser();

        Expense mappedExpense = new Expense();
        mappedExpense.setExpenseDate(expenseDate);

        ExpenseCategory expenseCategory = new ExpenseCategory();

        when(expenseMapper.toExpenseEntity(dto))
                .thenReturn(mappedExpense);

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(expenseCategory));

        when(expenseRepository.save(mappedExpense))
                .thenReturn(mappedExpense);

        when(expenseMapper.toExpenseResponseDto(mappedExpense))
                .thenReturn(new ExpenseResponseDto());

        expenseService.addExpense(
                dto,
                authenticatedUser);

        assertEquals(
                expenseDate,
                mappedExpense.getExpenseDate());
    }

    @Test
    void addExpense_shouldThrowWhenCategoryIsNotFound() {
        ExpenseRequestDto dto = createExpenseRequestDto();
        User authenticatedUser = createUser();

        Expense mappedExpense = new Expense();

        when(expenseMapper.toExpenseEntity(dto))
                .thenReturn(mappedExpense);

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.addExpense(
                        dto,
                        authenticatedUser));

        assertEquals(
                "Category Not found",
                exception.getMessage());

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void updateExpense_shouldUpdateFieldsAndSaveExpense() {
        ExpenseRequestDto dto = createExpenseRequestDto();

        LocalDate newExpenseDate = LocalDate.of(
                2026,
                8,
                18);

        dto.setExpenseDate(newExpenseDate);

        Expense expense = createExistingExpense();
        ExpenseCategory newCategory = new ExpenseCategory();
        ExpenseResponseDto responseDto = new ExpenseResponseDto();

        when(expenseRepository.findByIdAndUserId(
                EXPENSE_ID,
                USER_ID))
                .thenReturn(Optional.of(expense));

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(newCategory));

        when(expenseRepository.save(expense))
                .thenReturn(expense);

        when(expenseMapper.toExpenseResponseDto(expense))
                .thenReturn(responseDto);

        ExpenseResponseDto result = expenseService.updateExpense(
                dto,
                EXPENSE_ID,
                USER_ID);

        assertEquals(
                dto.getLabel(),
                expense.getLabel());

        assertEquals(
                dto.getAmount(),
                expense.getAmount());

        assertEquals(
                dto.getNote(),
                expense.getNote());

        assertEquals(
                newExpenseDate,
                expense.getExpenseDate());

        assertSame(
                newCategory,
                expense.getExpenseCategory());

        assertSame(
                responseDto,
                result);

        verify(expenseRepository).save(expense);
    }

    @Test
    void updateExpense_shouldKeepExistingDateWhenRequestDateIsNull() {
        ExpenseRequestDto dto = createExpenseRequestDto();
        dto.setExpenseDate(null);

        Expense expense = createExistingExpense();

        LocalDate existingDate = expense.getExpenseDate();

        ExpenseCategory newCategory = new ExpenseCategory();

        when(expenseRepository.findByIdAndUserId(
                EXPENSE_ID,
                USER_ID))
                .thenReturn(Optional.of(expense));

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(newCategory));

        when(expenseRepository.save(expense))
                .thenReturn(expense);

        when(expenseMapper.toExpenseResponseDto(expense))
                .thenReturn(new ExpenseResponseDto());

        expenseService.updateExpense(
                dto,
                EXPENSE_ID,
                USER_ID);

        assertEquals(
                existingDate,
                expense.getExpenseDate());
    }

    @Test
    void updateExpense_shouldThrowWhenExpenseIsNotFound() {
        ExpenseRequestDto dto = createExpenseRequestDto();

        when(expenseRepository.findByIdAndUserId(
                EXPENSE_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.updateExpense(
                        dto,
                        EXPENSE_ID,
                        USER_ID));

        assertEquals(
                "Expense not found",
                exception.getMessage());

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void updateExpense_shouldThrowWhenCategoryIsNotFound() {
        ExpenseRequestDto dto = createExpenseRequestDto();
        Expense expense = createExistingExpense();

        when(expenseRepository.findByIdAndUserId(
                EXPENSE_ID,
                USER_ID))
                .thenReturn(Optional.of(expense));

        when(expenseCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.updateExpense(
                        dto,
                        EXPENSE_ID,
                        USER_ID));

        assertEquals(
                "Category not found",
                exception.getMessage());

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void deleteExpense_shouldDeleteExpenseWhenItExists() {
        when(expenseRepository.deleteByIdAndUserId(
                EXPENSE_ID,
                USER_ID))
                .thenReturn(1L);

        expenseService.deleteExpense(
                EXPENSE_ID,
                USER_ID);

        verify(expenseRepository).deleteByIdAndUserId(
                EXPENSE_ID,
                USER_ID);
    }

    @Test
    void deleteExpense_shouldThrowWhenExpenseIsNotFound() {
        when(expenseRepository.deleteByIdAndUserId(
                EXPENSE_ID,
                USER_ID))
                .thenReturn(0L);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.deleteExpense(
                        EXPENSE_ID,
                        USER_ID));

        assertEquals(
                "Expense not found",
                exception.getMessage());
    }

    @Test
    void getExpenseDetail_shouldReturnMappedExpense() {
        Expense expense = new Expense();
        ExpenseResponseDto responseDto = new ExpenseResponseDto();

        when(expenseRepository.findByIdAndUserId(
                EXPENSE_ID,
                USER_ID))
                .thenReturn(Optional.of(expense));

        when(expenseMapper.toExpenseResponseDto(expense))
                .thenReturn(responseDto);

        ExpenseResponseDto result = expenseService.getExpenseDetail(
                EXPENSE_ID,
                USER_ID);

        assertSame(
                responseDto,
                result);
    }

    @Test
    void getExpenseDetail_shouldThrowWhenExpenseIsNotFound() {
        when(expenseRepository.findByIdAndUserId(
                EXPENSE_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.getExpenseDetail(
                        EXPENSE_ID,
                        USER_ID));

        assertEquals(
                "Expense not found",
                exception.getMessage());
    }

    @Test
    void getAllExpenses_shouldReturnMappedExpenses() {
        Expense expense1 = new Expense();
        Expense expense2 = new Expense();

        ExpenseResponseDto responseDto1 = new ExpenseResponseDto();
        ExpenseResponseDto responseDto2 = new ExpenseResponseDto();

        when(expenseRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(
                        expense1,
                        expense2));

        when(expenseMapper.toExpenseResponseDto(expense1))
                .thenReturn(responseDto1);

        when(expenseMapper.toExpenseResponseDto(expense2))
                .thenReturn(responseDto2);

        List<ExpenseResponseDto> result = expenseService.getAllExpenses(USER_ID);

        assertEquals(
                2,
                result.size());

        assertSame(
                responseDto1,
                result.get(0));

        assertSame(
                responseDto2,
                result.get(1));
    }

    private User createUser() {
        User user = new User();
        user.setId(USER_ID);

        return user;
    }

    private Expense createExistingExpense() {
        Expense expense = new Expense();

        expense.setId(EXPENSE_ID);
        expense.setLabel("Existing expense");
        expense.setAmount(new BigDecimal("20.00"));
        expense.setNote("Existing note");
        expense.setExpenseDate(
                LocalDate.of(2026, 8, 10));

        return expense;
    }

    private ExpenseRequestDto createExpenseRequestDto() {
        ExpenseRequestDto dto = new ExpenseRequestDto();

        dto.setLabel("Updated expense");
        dto.setAmount(new BigDecimal("42.50"));
        dto.setNote("Updated note");
        dto.setExpenseDate(
                LocalDate.of(2026, 8, 18));
        dto.setExpenseCategoryId(CATEGORY_ID);

        return dto;
    }
}