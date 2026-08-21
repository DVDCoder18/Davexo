package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.davexo.backend.dto.request.BudgetRequestDto;
import com.davexo.backend.dto.response.BudgetResponseDto;
import com.davexo.backend.entity.Budget;
import com.davexo.backend.entity.ExpenseCategory;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.BudgetScope;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.BudgetMapper;
import com.davexo.backend.repository.BudgetRepository;
import com.davexo.backend.repository.ExpenseCategoryRepository;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    private static final Integer USER_ID = 1;
    private static final Integer BUDGET_ID = 10;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    private BudgetMapper budgetMapper;

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService(
                budgetRepository,
                expenseCategoryRepository,
                budgetMapper);
    }

    @Test
    void createBudget_shouldRejectSecondGlobalBudget() {
        User user = createUser();

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of());

        when(budgetRepository.existsByScopeAndUserId(
                BudgetScope.GLOBAL,
                USER_ID))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.createBudget(
                        dto,
                        user));

        assertEquals(
                "You can only have one global budget",
                exception.getMessage());

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void createBudget_shouldCreateGlobalBudgetWhenNoCategoryIsSelected() {
        User user = createUser();

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of());

        Budget budget = new Budget();
        Budget savedBudget = new Budget();
        BudgetResponseDto responseDto = new BudgetResponseDto();

        when(budgetRepository.existsByScopeAndUserId(
                BudgetScope.GLOBAL,
                USER_ID))
                .thenReturn(false);

        when(budgetMapper.toBudgetEntity(dto))
                .thenReturn(budget);

        when(budgetRepository.save(budget))
                .thenReturn(savedBudget);

        when(budgetMapper.toBudgetResponseDto(
                savedBudget,
                List.of()))
                .thenReturn(responseDto);

        BudgetResponseDto result = budgetService.createBudget(
                dto,
                user);

        assertEquals(
                BudgetScope.GLOBAL,
                budget.getScope());

        assertSame(
                user,
                budget.getUser());

        assertSame(
                responseDto,
                result);
    }

    @Test
    void createBudget_shouldRejectSelectedCategoriesWhenOneIsNotFound() {
        User user = createUser();

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of(1, 2));

        ExpenseCategory category = createCategory(
                1,
                null);

        when(expenseCategoryRepository.findAllByIdInAndUserId(
                List.of(1, 2),
                USER_ID))
                .thenReturn(List.of(category));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> budgetService.createBudget(
                        dto,
                        user));

        assertEquals(
                "One or more expense categories were not found",
                exception.getMessage());

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void createBudget_shouldRejectCategoryAlreadyFollowedByAnotherBudget() {
        User user = createUser();

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of(1));

        Budget existingBudget = new Budget();
        existingBudget.setId(99);

        ExpenseCategory category = createCategory(
                1,
                existingBudget);

        when(expenseCategoryRepository.findAllByIdInAndUserId(
                List.of(1),
                USER_ID))
                .thenReturn(List.of(category));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.createBudget(
                        dto,
                        user));

        assertEquals(
                "One or more expense categories are already followed by another budget",
                exception.getMessage());

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void createBudget_shouldCreateSelectedCategoriesBudgetAndLinkCategories() {
        User user = createUser();

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of(1, 2));

        ExpenseCategory category1 = createCategory(
                1,
                null);

        ExpenseCategory category2 = createCategory(
                2,
                null);

        Budget budget = new Budget();

        Budget savedBudget = new Budget();
        savedBudget.setId(BUDGET_ID);

        BudgetResponseDto responseDto = new BudgetResponseDto();

        when(expenseCategoryRepository.findAllByIdInAndUserId(
                List.of(1, 2),
                USER_ID))
                .thenReturn(List.of(
                        category1,
                        category2));

        when(budgetMapper.toBudgetEntity(dto))
                .thenReturn(budget);

        when(budgetRepository.save(budget))
                .thenReturn(savedBudget);

        when(budgetMapper.toBudgetResponseDto(
                savedBudget,
                List.of(1, 2)))
                .thenReturn(responseDto);

        BudgetResponseDto result = budgetService.createBudget(
                dto,
                user);

        assertEquals(
                BudgetScope.SELECTED_CATEGORIES,
                budget.getScope());

        assertSame(
                user,
                budget.getUser());

        assertSame(
                savedBudget,
                category1.getBudget());

        assertSame(
                savedBudget,
                category2.getBudget());

        assertSame(
                responseDto,
                result);
    }

    @Test
    void updateBudget_shouldRejectGlobalScopeWhenAnotherGlobalBudgetExists() {
        Budget budget = createBudget(
                BUDGET_ID,
                BudgetScope.SELECTED_CATEGORIES);

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of());

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.of(budget));

        when(budgetRepository.existsByScopeAndUserIdAndIdNot(
                BudgetScope.GLOBAL,
                USER_ID,
                BUDGET_ID))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.updateBudget(
                        dto,
                        BUDGET_ID,
                        USER_ID));

        assertEquals(
                "You can only have one global budget",
                exception.getMessage());

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void updateBudget_shouldRejectCategoryLinkedToAnotherBudget() {
        Budget budget = createBudget(
                BUDGET_ID,
                BudgetScope.SELECTED_CATEGORIES);

        Budget otherBudget = createBudget(
                99,
                BudgetScope.SELECTED_CATEGORIES);

        ExpenseCategory category = createCategory(
                1,
                otherBudget);

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of(1));

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.of(budget));

        when(expenseCategoryRepository.findAllByBudgetIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(List.of());

        when(expenseCategoryRepository.findAllByIdInAndUserId(
                List.of(1),
                USER_ID))
                .thenReturn(List.of(category));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.updateBudget(
                        dto,
                        BUDGET_ID,
                        USER_ID));

        assertEquals(
                "One or more expense categories are already followed by another budget",
                exception.getMessage());

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void updateBudget_shouldAllowCategoryAlreadyLinkedToSameBudget() {
        Budget budget = createBudget(
                BUDGET_ID,
                BudgetScope.SELECTED_CATEGORIES);

        ExpenseCategory category = createCategory(
                1,
                budget);

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of(1));

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.of(budget));

        when(expenseCategoryRepository.findAllByBudgetIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(List.of(category));

        when(expenseCategoryRepository.findAllByIdInAndUserId(
                List.of(1),
                USER_ID))
                .thenReturn(List.of(category));

        when(budgetRepository.save(budget))
                .thenReturn(budget);

        when(budgetMapper.toBudgetResponseDto(
                budget,
                List.of(1)))
                .thenReturn(new BudgetResponseDto());

        budgetService.updateBudget(
                dto,
                BUDGET_ID,
                USER_ID);

        assertSame(
                budget,
                category.getBudget());

        assertEquals(
                BudgetScope.SELECTED_CATEGORIES,
                budget.getScope());

        verify(budgetRepository).save(budget);
    }

    @Test
    void updateBudget_shouldReplaceFollowedCategories() {
        Budget budget = createBudget(
                BUDGET_ID,
                BudgetScope.SELECTED_CATEGORIES);

        ExpenseCategory oldCategory = createCategory(
                1,
                budget);

        ExpenseCategory newCategory = createCategory(
                2,
                null);

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of(2));

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.of(budget));

        when(expenseCategoryRepository.findAllByBudgetIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(List.of(oldCategory));

        when(expenseCategoryRepository.findAllByIdInAndUserId(
                List.of(2),
                USER_ID))
                .thenReturn(List.of(newCategory));

        when(budgetRepository.save(budget))
                .thenReturn(budget);

        when(budgetMapper.toBudgetResponseDto(
                budget,
                List.of(2)))
                .thenReturn(new BudgetResponseDto());

        budgetService.updateBudget(
                dto,
                BUDGET_ID,
                USER_ID);

        assertNull(oldCategory.getBudget());

        assertSame(
                budget,
                newCategory.getBudget());

        assertEquals(
                BudgetScope.SELECTED_CATEGORIES,
                budget.getScope());
    }

    @Test
    void updateBudget_shouldDetachCategoriesWhenChangingToGlobalScope() {
        Budget budget = createBudget(
                BUDGET_ID,
                BudgetScope.SELECTED_CATEGORIES);

        ExpenseCategory category1 = createCategory(
                1,
                budget);

        ExpenseCategory category2 = createCategory(
                2,
                budget);

        BudgetRequestDto dto = createBudgetRequestDto(
                List.of());

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.of(budget));

        when(budgetRepository.existsByScopeAndUserIdAndIdNot(
                BudgetScope.GLOBAL,
                USER_ID,
                BUDGET_ID))
                .thenReturn(false);

        when(expenseCategoryRepository.findAllByBudgetIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(List.of(
                        category1,
                        category2));

        when(budgetRepository.save(budget))
                .thenReturn(budget);

        when(budgetMapper.toBudgetResponseDto(
                budget,
                List.of()))
                .thenReturn(new BudgetResponseDto());

        budgetService.updateBudget(
                dto,
                BUDGET_ID,
                USER_ID);

        assertEquals(
                BudgetScope.GLOBAL,
                budget.getScope());

        assertNull(category1.getBudget());
        assertNull(category2.getBudget());
    }

    @Test
    void deleteBudget_shouldDetachCategoriesBeforeDeletingSelectedBudget() {
        Budget budget = createBudget(
                BUDGET_ID,
                BudgetScope.SELECTED_CATEGORIES);

        ExpenseCategory category1 = createCategory(
                1,
                budget);

        ExpenseCategory category2 = createCategory(
                2,
                budget);

        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.of(budget));

        when(expenseCategoryRepository.findAllByBudgetIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(List.of(
                        category1,
                        category2));

        budgetService.deleteBudget(
                BUDGET_ID,
                USER_ID);

        assertNull(category1.getBudget());
        assertNull(category2.getBudget());

        verify(budgetRepository).delete(budget);
    }

    @Test
    void deleteBudget_shouldThrowWhenBudgetIsNotFound() {
        when(budgetRepository.findByIdAndUserId(
                BUDGET_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> budgetService.deleteBudget(
                        BUDGET_ID,
                        USER_ID));

        assertEquals(
                "Budget not found",
                exception.getMessage());

        verify(budgetRepository, never()).delete(any(Budget.class));
    }

    private User createUser() {
        User user = new User();
        user.setId(USER_ID);

        return user;
    }

    private Budget createBudget(
            Integer id,
            BudgetScope scope) {

        Budget budget = new Budget();

        budget.setId(id);
        budget.setScope(scope);
        budget.setName("Budget");
        budget.setAmount(new BigDecimal("500.00"));

        return budget;
    }

    private ExpenseCategory createCategory(
            Integer id,
            Budget budget) {

        ExpenseCategory category = new ExpenseCategory();

        category.setId(id);
        category.setBudget(budget);

        return category;
    }

    private BudgetRequestDto createBudgetRequestDto(
            List<Integer> followedCategoryIds) {

        BudgetRequestDto dto = new BudgetRequestDto();

        dto.setName("Monthly budget");
        dto.setAmount(new BigDecimal("500.00"));
        dto.setFollowedCategoryIds(followedCategoryIds);

        return dto;
    }
}