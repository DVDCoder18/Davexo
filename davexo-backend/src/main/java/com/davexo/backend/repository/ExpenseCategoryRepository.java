package com.davexo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davexo.backend.entity.ExpenseCategory;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Integer> {

    Optional<ExpenseCategory> findByIdAndUserId(Integer expenseCategoryId, Integer userId);

    List<ExpenseCategory> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer expenseCategoryId, Integer userId);

    boolean existsByNameIgnoreCaseAndUserId(String name, Integer userId);
}
