package com.davexo.backend.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davexo.backend.dto.response.BudgetConsumptionResponseDto;
import com.davexo.backend.dto.response.DashboardResponseDto;
import com.davexo.backend.dto.response.ExpenseResponseDto;
import com.davexo.backend.dto.response.InventoryItemResponseDto;
import com.davexo.backend.dto.response.TaskResponseDto;
import com.davexo.backend.entity.Budget;
import com.davexo.backend.entity.InventoryItem;
import com.davexo.backend.enums.BudgetScope;
import com.davexo.backend.enums.TaskStatus;
import com.davexo.backend.mapper.ExpenseMapper;
import com.davexo.backend.mapper.InventoryItemMapper;
import com.davexo.backend.mapper.TaskMapper;
import com.davexo.backend.repository.BudgetRepository;
import com.davexo.backend.repository.ExpenseRepository;
import com.davexo.backend.repository.InventoryItemRepository;
import com.davexo.backend.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final TaskRepository taskRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final BudgetRepository budgetRepository;

    private final ExpenseMapper expenseMapper;
    private final TaskMapper taskMapper;
    private final InventoryItemMapper inventoryItemMapper;

    private final BudgetService budgetService;

    private final Clock clock;

    @Transactional(readOnly = true)
    public DashboardResponseDto getDashboard(Integer userId) {

        LocalDate today = LocalDate.now(clock);
        LocalDate startOfMonth = today.withDayOfMonth(1);

        BigDecimal currentMonthExpensesTotal = expenseRepository.sumAmountByUserAndPeriod(userId, startOfMonth, today);

        long totalTasksToDo = taskRepository.countByUserIdAndStatus(userId, TaskStatus.TO_DO);

        List<TaskResponseDto> tasksToDo = taskRepository.findDashboardTasksByUserId(userId)
                .stream()
                .limit(5)
                .map(taskMapper::toTaskResponseDto)
                .toList();

        List<InventoryItem> shoppingList = inventoryItemRepository.findShoppingListByUserId(userId);

        long totalShoppingItems = shoppingList.size();

        List<InventoryItemResponseDto> shoppingItems = shoppingList.stream()
                .limit(5)
                .map(inventoryItemMapper::toInventoryItemResponseDto)
                .toList();

        List<ExpenseResponseDto> recentExpenses = expenseRepository
                .findTop10ByUserIdOrderByExpenseDateDescIdDesc(userId)
                .stream()
                .map(expenseMapper::toExpenseResponseDto)
                .toList();

        BudgetConsumptionResponseDto globalBudgetConsumption = getGlobalBudgetConsumption(userId);

        return DashboardResponseDto.builder()
                .currentMonthExpensesTotal(currentMonthExpensesTotal)
                .totalTasksToDo(totalTasksToDo)
                .totalShoppingItems(totalShoppingItems)
                .globalBudgetConsumption(globalBudgetConsumption)
                .tasksToDo(tasksToDo)
                .shoppingItems(shoppingItems)
                .recentExpenses(recentExpenses)
                .build();
    }

    private BudgetConsumptionResponseDto getGlobalBudgetConsumption(Integer userId) {

        return budgetRepository.findByScopeAndUserId(BudgetScope.GLOBAL, userId)
                .map(Budget::getId)
                .map(budgetId -> budgetService.getBudgetConsumption(budgetId, userId))
                .orElse(null);
    }
}