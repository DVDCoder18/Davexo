package com.davexo.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.davexo.backend.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    Optional<Expense> findByIdAndUserId(Integer expenseId, Integer userId);

    List<Expense> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer expenseId, Integer userId);

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
        AND e.expenseDate BETWEEN :startDate AND :endDate
        """)
    BigDecimal sumAmountByUserAndPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("""
            SELECT e.expenseCategory.id,
                e.expenseCategory.name,
                SUM(e.amount)
            FROM Expense e
            WHERE e.user.id = :userId
            AND e.expenseDate BETWEEN :startDate AND :endDate
            GROUP BY e.expenseCategory.id, e.expenseCategory.name
            ORDER BY SUM(e.amount) DESC
            """)
    List<Object[]> sumAmountByCategoryAndPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("""
            SELECT MIN(e.expenseDate)
            FROM Expense e
            WHERE e.user.id = :userId
            """)
    Optional<LocalDate> findFirstExpenseDateByUserId(
            @Param("userId") Integer userId);
}
