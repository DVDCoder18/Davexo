package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.davexo.backend.dto.request.BudgetRequestDto;
import com.davexo.backend.entity.Budget;
import com.davexo.backend.entity.ExpenseCategory;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.BudgetScope;
import com.davexo.backend.enums.Role;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.integration.AbstractIntegrationTest;
import com.davexo.backend.repository.BudgetRepository;
import com.davexo.backend.repository.ExpenseCategoryRepository;

import jakarta.persistence.EntityManager;

@Transactional
class BudgetServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createBudget_shouldPersistSelectedCategoriesAssociations() {
        User user = createUser(
                "user@test.com",
                "User");

        ExpenseCategory food = createCategory(
                "Food",
                user);

        ExpenseCategory transport = createCategory(
                "Transport",
                user);

        BudgetRequestDto dto = createBudgetRequestDto(
                "Essential expenses",
                new BigDecimal("600.00"),
                List.of(
                        food.getId(),
                        transport.getId()));

        budgetService.createBudget(
                dto,
                user);

        entityManager.flush();
        entityManager.clear();

        List<Budget> budgets = budgetRepository.findAllByUserId(
                user.getId());

        assertEquals(
                1,
                budgets.size());

        Budget savedBudget = budgets.get(0);

        assertEquals(
                BudgetScope.SELECTED_CATEGORIES,
                savedBudget.getScope());

        assertEquals(
                "Essential expenses",
                savedBudget.getName());

        assertEquals(
                new BigDecimal("600.00"),
                savedBudget.getAmount());

        List<ExpenseCategory> followedCategories = expenseCategoryRepository.findAllByBudgetIdAndUserId(
                savedBudget.getId(),
                user.getId());

        assertEquals(
                2,
                followedCategories.size());

        assertTrue(
                followedCategories.stream()
                        .anyMatch(category -> category.getName().equals("Food")));

        assertTrue(
                followedCategories.stream()
                        .anyMatch(category -> category.getName().equals("Transport")));
    }

    @Test
    void createBudget_shouldRejectSecondGlobalBudgetForSameUser() {
        User user = createUser(
                "user@test.com",
                "User");

        BudgetRequestDto firstBudgetDto = createBudgetRequestDto(
                "Global budget",
                new BigDecimal("1500.00"),
                List.of());

        budgetService.createBudget(
                firstBudgetDto,
                user);

        entityManager.flush();

        BudgetRequestDto secondBudgetDto = createBudgetRequestDto(
                "Second global budget",
                new BigDecimal("1800.00"),
                List.of());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.createBudget(
                        secondBudgetDto,
                        user));

        assertEquals(
                "You can only have one global budget",
                exception.getMessage());

        assertEquals(
                1,
                budgetRepository.findAllByUserId(user.getId()).size());
    }

    @Test
    void createBudget_shouldNotAllowUsingAnotherUsersCategory() {
        User user1 = createUser(
                "user1@test.com",
                "User 1");

        User user2 = createUser(
                "user2@test.com",
                "User 2");

        ExpenseCategory user2Category = createCategory(
                "Private category",
                user2);

        BudgetRequestDto dto = createBudgetRequestDto(
                "Budget",
                new BigDecimal("500.00"),
                List.of(user2Category.getId()));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> budgetService.createBudget(
                        dto,
                        user1));

        assertEquals(
                "One or more expense categories were not found",
                exception.getMessage());

        assertTrue(
                budgetRepository.findAllByUserId(
                        user1.getId())
                        .isEmpty());
    }

    @Test
    void createBudget_shouldRejectCategoryAlreadyFollowedByAnotherBudget() {
        User user = createUser(
                "user@test.com",
                "User");

        ExpenseCategory category = createCategory(
                "Food",
                user);

        BudgetRequestDto firstBudgetDto = createBudgetRequestDto(
                "Food budget",
                new BigDecimal("400.00"),
                List.of(category.getId()));

        budgetService.createBudget(
                firstBudgetDto,
                user);

        entityManager.flush();
        entityManager.clear();

        ExpenseCategory persistedCategory = expenseCategoryRepository.findByIdAndUserId(
                category.getId(),
                user.getId())
                .orElseThrow();

        BudgetRequestDto secondBudgetDto = createBudgetRequestDto(
                "Another budget",
                new BigDecimal("600.00"),
                List.of(persistedCategory.getId()));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> budgetService.createBudget(
                        secondBudgetDto,
                        user));

        assertEquals(
                "One or more expense categories are already followed by another budget",
                exception.getMessage());

        assertEquals(
                1,
                budgetRepository.findAllByUserId(user.getId()).size());
    }

    @Test
    void updateBudget_shouldReplacePersistedFollowedCategories() {
        User user = createUser(
                "user@test.com",
                "User");

        ExpenseCategory food = createCategory(
                "Food",
                user);

        ExpenseCategory transport = createCategory(
                "Transport",
                user);

        BudgetRequestDto createDto = createBudgetRequestDto(
                "Initial budget",
                new BigDecimal("500.00"),
                List.of(food.getId()));

        budgetService.createBudget(
                createDto,
                user);

        entityManager.flush();
        entityManager.clear();

        Budget budget = budgetRepository.findAllByUserId(
                user.getId())
                .get(0);

        BudgetRequestDto updateDto = createBudgetRequestDto(
                "Updated budget",
                new BigDecimal("700.00"),
                List.of(transport.getId()));

        budgetService.updateBudget(
                updateDto,
                budget.getId(),
                user.getId());

        entityManager.flush();
        entityManager.clear();

        ExpenseCategory persistedFood = expenseCategoryRepository.findByIdAndUserId(
                food.getId(),
                user.getId())
                .orElseThrow();

        ExpenseCategory persistedTransport = expenseCategoryRepository.findByIdAndUserId(
                transport.getId(),
                user.getId())
                .orElseThrow();

        Budget persistedBudget = budgetRepository.findByIdAndUserId(
                budget.getId(),
                user.getId())
                .orElseThrow();

        assertNull(
                persistedFood.getBudget());

        assertEquals(
                persistedBudget.getId(),
                persistedTransport.getBudget().getId());

        assertEquals(
                "Updated budget",
                persistedBudget.getName());

        assertEquals(
                new BigDecimal("700.00"),
                persistedBudget.getAmount());

        assertEquals(
                BudgetScope.SELECTED_CATEGORIES,
                persistedBudget.getScope());
    }

    @Test
    void updateBudget_shouldDetachCategoriesWhenChangingToGlobal() {
        User user = createUser(
                "user@test.com",
                "User");

        ExpenseCategory food = createCategory(
                "Food",
                user);

        ExpenseCategory transport = createCategory(
                "Transport",
                user);

        BudgetRequestDto createDto = createBudgetRequestDto(
                "Specific budget",
                new BigDecimal("600.00"),
                List.of(
                        food.getId(),
                        transport.getId()));

        budgetService.createBudget(
                createDto,
                user);

        entityManager.flush();
        entityManager.clear();

        Budget budget = budgetRepository.findAllByUserId(
                user.getId())
                .get(0);

        BudgetRequestDto updateDto = createBudgetRequestDto(
                "Global budget",
                new BigDecimal("1500.00"),
                List.of());

        budgetService.updateBudget(
                updateDto,
                budget.getId(),
                user.getId());

        entityManager.flush();
        entityManager.clear();

        Budget persistedBudget = budgetRepository.findByIdAndUserId(
                budget.getId(),
                user.getId())
                .orElseThrow();

        ExpenseCategory persistedFood = expenseCategoryRepository.findByIdAndUserId(
                food.getId(),
                user.getId())
                .orElseThrow();

        ExpenseCategory persistedTransport = expenseCategoryRepository.findByIdAndUserId(
                transport.getId(),
                user.getId())
                .orElseThrow();

        assertEquals(
                BudgetScope.GLOBAL,
                persistedBudget.getScope());

        assertNull(
                persistedFood.getBudget());

        assertNull(
                persistedTransport.getBudget());
    }

    @Test
    void deleteBudget_shouldDetachCategoriesAndDeleteBudget() {
        User user = createUser(
                "user@test.com",
                "User");

        ExpenseCategory category = createCategory(
                "Food",
                user);

        BudgetRequestDto dto = createBudgetRequestDto(
                "Food budget",
                new BigDecimal("400.00"),
                List.of(category.getId()));

        budgetService.createBudget(
                dto,
                user);

        entityManager.flush();
        entityManager.clear();

        Budget budget = budgetRepository.findAllByUserId(
                user.getId())
                .get(0);

        budgetService.deleteBudget(
                budget.getId(),
                user.getId());

        entityManager.flush();
        entityManager.clear();

        assertFalse(
                budgetRepository.findByIdAndUserId(
                        budget.getId(),
                        user.getId())
                        .isPresent());

        ExpenseCategory persistedCategory = expenseCategoryRepository.findByIdAndUserId(
                category.getId(),
                user.getId())
                .orElseThrow();

        assertNull(
                persistedCategory.getBudget());
    }

    private User createUser(
            String email,
            String pseudo) {

        User user = new User();

        user.setPseudo(pseudo);
        user.setEmail(email);
        user.setPasswordHash("encoded-password");
        user.setCreatedAt(
                LocalDate.of(2026, 1, 1));
        user.setRole(Role.USER);
        user.setIsActive(true);

        entityManager.persist(user);

        return user;
    }

    private ExpenseCategory createCategory(
            String name,
            User user) {

        ExpenseCategory category = new ExpenseCategory();

        category.setName(name);
        category.setUser(user);

        entityManager.persist(category);

        return category;
    }

    private BudgetRequestDto createBudgetRequestDto(
            String name,
            BigDecimal amount,
            List<Integer> followedCategoryIds) {

        BudgetRequestDto dto = new BudgetRequestDto();

        dto.setName(name);
        dto.setAmount(amount);
        dto.setFollowedCategoryIds(followedCategoryIds);

        return dto;
    }
}