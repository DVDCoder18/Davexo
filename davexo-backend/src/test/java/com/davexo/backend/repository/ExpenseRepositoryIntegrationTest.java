package com.davexo.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.davexo.backend.entity.Expense;
import com.davexo.backend.entity.ExpenseCategory;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.Role;
import com.davexo.backend.integration.AbstractIntegrationTest;

import jakarta.persistence.EntityManager;

@Transactional
class ExpenseRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void sumAmountByUserAndPeriod_shouldSumOnlyUserExpensesInsidePeriod() {
        User user1 = createUser(
                "user1@test.com",
                "User 1");

        User user2 = createUser(
                "user2@test.com",
                "User 2");

        ExpenseCategory category1 = createCategory(
                "Food",
                user1);

        ExpenseCategory category2 = createCategory(
                "Food",
                user2);

        createExpense(
                "Expense 1",
                new BigDecimal("50.00"),
                LocalDate.of(2026, 8, 1),
                user1,
                category1);

        createExpense(
                "Expense 2",
                new BigDecimal("75.50"),
                LocalDate.of(2026, 8, 20),
                user1,
                category1);

        createExpense(
                "Outside period",
                new BigDecimal("100.00"),
                LocalDate.of(2026, 7, 31),
                user1,
                category1);

        createExpense(
                "Other user",
                new BigDecimal("500.00"),
                LocalDate.of(2026, 8, 10),
                user2,
                category2);

        entityManager.flush();

        BigDecimal result = expenseRepository.sumAmountByUserAndPeriod(
                user1.getId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 20));

        assertEquals(
                new BigDecimal("125.50"),
                result);
    }

    @Test
    void sumAmountByUserAndPeriod_shouldReturnZeroWhenNoExpenseMatches() {
        User user = createUser(
                "user@test.com",
                "User");

        entityManager.flush();

        BigDecimal result = expenseRepository.sumAmountByUserAndPeriod(
                user.getId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31));

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void sumAmountByCategoryAndPeriod_shouldGroupByCategoryAndOrderByTotalDescending() {
        User user = createUser(
                "user@test.com",
                "User");

        ExpenseCategory food = createCategory(
                "Food",
                user);

        ExpenseCategory transport = createCategory(
                "Transport",
                user);

        createExpense(
                "Groceries",
                new BigDecimal("100.00"),
                LocalDate.of(2026, 8, 5),
                user,
                food);

        createExpense(
                "Restaurant",
                new BigDecimal("50.00"),
                LocalDate.of(2026, 8, 10),
                user,
                food);

        createExpense(
                "Train",
                new BigDecimal("80.00"),
                LocalDate.of(2026, 8, 12),
                user,
                transport);

        createExpense(
                "Old expense",
                new BigDecimal("1000.00"),
                LocalDate.of(2026, 7, 15),
                user,
                transport);

        entityManager.flush();

        List<Object[]> result = expenseRepository.sumAmountByCategoryAndPeriod(
                user.getId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31));

        assertEquals(
                2,
                result.size());

        assertEquals(
                food.getId(),
                result.get(0)[0]);

        assertEquals(
                "Food",
                result.get(0)[1]);

        assertEquals(
                new BigDecimal("150.00"),
                result.get(0)[2]);

        assertEquals(
                transport.getId(),
                result.get(1)[0]);

        assertEquals(
                "Transport",
                result.get(1)[1]);

        assertEquals(
                new BigDecimal("80.00"),
                result.get(1)[2]);
    }

    @Test
    void findFirstExpenseDateByUserId_shouldReturnOnlyUsersEarliestExpenseDate() {
        User user1 = createUser(
                "user1@test.com",
                "User 1");

        User user2 = createUser(
                "user2@test.com",
                "User 2");

        ExpenseCategory category1 = createCategory(
                "Food",
                user1);

        ExpenseCategory category2 = createCategory(
                "Food",
                user2);

        createExpense(
                "Later expense",
                new BigDecimal("20.00"),
                LocalDate.of(2026, 6, 15),
                user1,
                category1);

        createExpense(
                "First expense",
                new BigDecimal("30.00"),
                LocalDate.of(2026, 4, 10),
                user1,
                category1);

        createExpense(
                "Other user earlier expense",
                new BigDecimal("40.00"),
                LocalDate.of(2025, 1, 1),
                user2,
                category2);

        entityManager.flush();

        LocalDate result = expenseRepository
                .findFirstExpenseDateByUserId(user1.getId())
                .orElseThrow();

        assertEquals(
                LocalDate.of(2026, 4, 10),
                result);

        assertTrue(
                expenseRepository
                        .findFirstExpenseDateByUserId(user1.getId())
                        .isPresent());
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

    private Expense createExpense(
            String label,
            BigDecimal amount,
            LocalDate expenseDate,
            User user,
            ExpenseCategory category) {

        Expense expense = new Expense();

        expense.setLabel(label);
        expense.setAmount(amount);
        expense.setExpenseDate(expenseDate);
        expense.setUser(user);
        expense.setExpenseCategory(category);

        entityManager.persist(expense);

        return expense;
    }
}