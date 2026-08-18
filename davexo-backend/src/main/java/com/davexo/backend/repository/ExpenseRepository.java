package com.davexo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davexo.backend.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    Optional<Expense> findByIdAndUserId(Integer expenseId, Integer userId);

    List<Expense> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer expenseId, Integer userId);
}
