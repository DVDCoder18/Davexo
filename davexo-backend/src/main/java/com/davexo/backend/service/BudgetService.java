package com.davexo.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.request.BudgetRequestDto;
import com.davexo.backend.dto.response.BudgetConsumptionResponseDto;
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
import com.davexo.backend.repository.ExpenseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetMapper budgetMapper;
    private final Clock clock;

    @Transactional
    public BudgetResponseDto createBudget(
            BudgetRequestDto budgetRequestDto,
            User authenticatedUser) {

        List<Integer> followedCategoryIds = budgetRequestDto.getFollowedCategoryIds();

        BudgetScope scope = followedCategoryIds.isEmpty()
                ? BudgetScope.GLOBAL
                : BudgetScope.SELECTED_CATEGORIES;

        if (scope == BudgetScope.GLOBAL
                && budgetRepository.existsByScopeAndUserId(
                        BudgetScope.GLOBAL,
                        authenticatedUser.getId())) {

            throw new BusinessException("You can only have one global budget");
        }

        List<ExpenseCategory> followedCategories = List.of();

        if (scope == BudgetScope.SELECTED_CATEGORIES) {

            followedCategories = expenseCategoryRepository.findAllByIdInAndUserId(
                    followedCategoryIds,
                    authenticatedUser.getId());

            if (followedCategories.size() != followedCategoryIds.size()) {
                throw new ResourceNotFoundException(
                        "One or more expense categories were not found");
            }

            for (ExpenseCategory expenseCategory : followedCategories) {
                if (expenseCategory.getBudget() != null) {
                    throw new BusinessException(
                            "One or more expense categories are already followed by another budget");
                }
            }
        }

        Budget budget = budgetMapper.toBudgetEntity(budgetRequestDto);

        budget.setUser(authenticatedUser);
        budget.setScope(scope);

        Budget savedBudget = budgetRepository.save(budget);

        if (scope == BudgetScope.SELECTED_CATEGORIES) {
            for (ExpenseCategory expenseCategory : followedCategories) {
                expenseCategory.setBudget(savedBudget);
            }
        }

        return budgetMapper.toBudgetResponseDto(
                savedBudget,
                followedCategoryIds);
    }

    @Transactional
    public BudgetResponseDto updateBudget(
            BudgetRequestDto budgetRequestDto,
            Integer budgetId,
            Integer userId) {

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        List<Integer> followedCategoryIds = budgetRequestDto.getFollowedCategoryIds();

        BudgetScope newScope = followedCategoryIds.isEmpty()
                ? BudgetScope.GLOBAL
                : BudgetScope.SELECTED_CATEGORIES;

        if (newScope == BudgetScope.GLOBAL
                && budgetRepository.existsByScopeAndUserIdAndIdNot(
                        BudgetScope.GLOBAL,
                        userId,
                        budgetId)) {

            throw new BusinessException("You can only have one global budget");
        }

        List<ExpenseCategory> currentFollowedCategories = expenseCategoryRepository
                .findAllByBudgetIdAndUserId(
                        budgetId,
                        userId);

        List<ExpenseCategory> newFollowedCategories = List.of();

        if (newScope == BudgetScope.SELECTED_CATEGORIES) {

            newFollowedCategories = expenseCategoryRepository.findAllByIdInAndUserId(
                    followedCategoryIds,
                    userId);

            if (newFollowedCategories.size() != followedCategoryIds.size()) {
                throw new ResourceNotFoundException(
                        "One or more expense categories were not found");
            }

            for (ExpenseCategory expenseCategory : newFollowedCategories) {

                if (expenseCategory.getBudget() != null
                        && !expenseCategory.getBudget().getId().equals(budgetId)) {

                    throw new BusinessException(
                            "One or more expense categories are already followed by another budget");
                }
            }
        }

        budget.setName(budgetRequestDto.getName());
        budget.setAmount(budgetRequestDto.getAmount());
        budget.setScope(newScope);

        Budget savedBudget = budgetRepository.save(budget);

        for (ExpenseCategory expenseCategory : currentFollowedCategories) {
            expenseCategory.setBudget(null);
        }

        if (newScope == BudgetScope.SELECTED_CATEGORIES) {
            for (ExpenseCategory expenseCategory : newFollowedCategories) {
                expenseCategory.setBudget(savedBudget);
            }
        }

        return budgetMapper.toBudgetResponseDto(
                savedBudget,
                followedCategoryIds);
    }

    @Transactional
    public BudgetResponseDto getBudgetDetail(
            Integer budgetId,
            Integer userId) {

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        List<Integer> followedCategoryIds = expenseCategoryRepository
                .findAllByBudgetIdAndUserId(budgetId, userId)
                .stream()
                .map(ExpenseCategory::getId)
                .toList();

        return budgetMapper.toBudgetResponseDto(
                budget,
                followedCategoryIds);
    }

    @Transactional
    public List<BudgetResponseDto> getAllBudgets(Integer userId) {

        List<Budget> budgetList = budgetRepository.findAllByUserId(userId);

        if (budgetList.isEmpty()) {
            return List.of();
        }

        List<Integer> budgetIds = budgetList.stream()
                .map(Budget::getId)
                .toList();

        List<ExpenseCategory> followedCategories = expenseCategoryRepository
                .findAllByBudgetIdInAndUserId(
                        budgetIds,
                        userId);

        return budgetList.stream()
                .map(budget -> {

                    List<Integer> followedCategoryIds = followedCategories.stream()
                            .filter(category -> category.getBudget() != null
                                    && category.getBudget().getId()
                                            .equals(budget.getId()))
                            .map(ExpenseCategory::getId)
                            .toList();

                    return budgetMapper.toBudgetResponseDto(
                            budget,
                            followedCategoryIds);
                })
                .toList();
    }

    @Transactional
    public BudgetConsumptionResponseDto getBudgetConsumption(
            Integer budgetId,
            Integer userId) {

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        LocalDate today = LocalDate.now(clock);
        LocalDate startDate = today.withDayOfMonth(1);

        return buildBudgetConsumption(
                budget,
                userId,
                startDate,
                today);
    }

    @Transactional
    public List<BudgetConsumptionResponseDto> getAllBudgetConsumptions(
            Integer userId) {

        List<Budget> budgets = budgetRepository.findAllByUserId(userId);

        LocalDate today = LocalDate.now(clock);
        LocalDate startDate = today.withDayOfMonth(1);

        return budgets.stream()
                .map(budget -> buildBudgetConsumption(
                        budget,
                        userId,
                        startDate,
                        today))
                .toList();
    }

    private BudgetConsumptionResponseDto buildBudgetConsumption(
            Budget budget,
            Integer userId,
            LocalDate startDate,
            LocalDate endDate) {

        BigDecimal spentAmount;

        if (budget.getScope() == BudgetScope.GLOBAL) {

            spentAmount = expenseRepository.sumAmountByUserAndPeriod(
                    userId,
                    startDate,
                    endDate);

        } else {

            spentAmount = expenseRepository.sumAmountByUserAndBudgetAndPeriod(
                    userId,
                    budget.getId(),
                    startDate,
                    endDate);
        }

        BigDecimal remainingAmount = budget.getAmount()
                .subtract(spentAmount);

        BigDecimal consumptionPercentage = null;

        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {

            consumptionPercentage = spentAmount
                    .multiply(new BigDecimal("100"))
                    .divide(
                            budget.getAmount(),
                            2,
                            RoundingMode.HALF_UP);
        }

        return BudgetConsumptionResponseDto.builder()
                .budgetId(budget.getId())
                .budgetAmount(budget.getAmount())
                .spentAmount(spentAmount)
                .remainingAmount(remainingAmount)
                .consumptionPercentage(consumptionPercentage)
                .build();
    }

    @Transactional
    public void deleteBudget(
            Integer budgetId,
            Integer userId) {

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (budget.getScope() == BudgetScope.SELECTED_CATEGORIES) {

            List<ExpenseCategory> followedCategories = expenseCategoryRepository
                    .findAllByBudgetIdAndUserId(
                            budgetId,
                            userId);

            for (ExpenseCategory expenseCategory : followedCategories) {
                expenseCategory.setBudget(null);
            }
        }

        budgetRepository.delete(budget);
    }
}