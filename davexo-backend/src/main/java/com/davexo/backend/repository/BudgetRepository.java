package com.davexo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davexo.backend.entity.Budget;
import com.davexo.backend.enums.BudgetScope;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    Optional<Budget> findByIdAndUserId(Integer budgetId, Integer userId);

    List<Budget> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer budgetId, Integer userId);

    boolean existsByScopeAndUserId(BudgetScope scope, Integer userId);

    boolean existsByScopeAndUserIdAndIdNot(BudgetScope scope, Integer userId, Integer budgetId);

    Optional<Budget> findByScopeAndUserId(BudgetScope scope, Integer userId);
}
