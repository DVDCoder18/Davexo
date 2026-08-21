package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
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

import com.davexo.backend.dto.response.BudgetConsumptionResponseDto;
import com.davexo.backend.dto.response.DashboardResponseDto;
import com.davexo.backend.dto.response.ExpenseResponseDto;
import com.davexo.backend.dto.response.InventoryItemResponseDto;
import com.davexo.backend.dto.response.TaskResponseDto;
import com.davexo.backend.entity.Budget;
import com.davexo.backend.entity.Expense;
import com.davexo.backend.entity.InventoryItem;
import com.davexo.backend.entity.Task;
import com.davexo.backend.enums.BudgetScope;
import com.davexo.backend.enums.TaskStatus;
import com.davexo.backend.mapper.ExpenseMapper;
import com.davexo.backend.mapper.InventoryItemMapper;
import com.davexo.backend.mapper.TaskMapper;
import com.davexo.backend.repository.BudgetRepository;
import com.davexo.backend.repository.ExpenseRepository;
import com.davexo.backend.repository.InventoryItemRepository;
import com.davexo.backend.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private InventoryItemMapper inventoryItemMapper;

    @Mock
    private BudgetService budgetService;

    private DashboardService dashboardService;

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-08-21T12:00:00Z"),
            ZoneId.of("Europe/Paris"));

    @BeforeEach
    void setUp() {

        dashboardService = new DashboardService(
                expenseRepository,
                taskRepository,
                inventoryItemRepository,
                budgetRepository,
                expenseMapper,
                taskMapper,
                inventoryItemMapper,
                budgetService,
                clock);
    }

    @Test
    void getDashboard_shouldReturnCompleteDashboard() {

        Integer userId = 1;

        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);
        Task task3 = mock(Task.class);
        Task task4 = mock(Task.class);
        Task task5 = mock(Task.class);
        Task task6 = mock(Task.class);

        TaskResponseDto taskDto1 = mock(TaskResponseDto.class);
        TaskResponseDto taskDto2 = mock(TaskResponseDto.class);
        TaskResponseDto taskDto3 = mock(TaskResponseDto.class);
        TaskResponseDto taskDto4 = mock(TaskResponseDto.class);
        TaskResponseDto taskDto5 = mock(TaskResponseDto.class);

        InventoryItem item1 = mock(InventoryItem.class);
        InventoryItem item2 = mock(InventoryItem.class);
        InventoryItem item3 = mock(InventoryItem.class);
        InventoryItem item4 = mock(InventoryItem.class);
        InventoryItem item5 = mock(InventoryItem.class);
        InventoryItem item6 = mock(InventoryItem.class);

        InventoryItemResponseDto itemDto1 = mock(InventoryItemResponseDto.class);
        InventoryItemResponseDto itemDto2 = mock(InventoryItemResponseDto.class);
        InventoryItemResponseDto itemDto3 = mock(InventoryItemResponseDto.class);
        InventoryItemResponseDto itemDto4 = mock(InventoryItemResponseDto.class);
        InventoryItemResponseDto itemDto5 = mock(InventoryItemResponseDto.class);

        Expense expense1 = mock(Expense.class);
        Expense expense2 = mock(Expense.class);

        ExpenseResponseDto expenseDto1 = mock(ExpenseResponseDto.class);
        ExpenseResponseDto expenseDto2 = mock(ExpenseResponseDto.class);

        Budget globalBudget = mock(Budget.class);

        BudgetConsumptionResponseDto budgetConsumption = BudgetConsumptionResponseDto.builder()
                .budgetId(10)
                .budgetAmount(new BigDecimal("1800.00"))
                .spentAmount(new BigDecimal("1003.74"))
                .remainingAmount(new BigDecimal("796.26"))
                .consumptionPercentage(new BigDecimal("55.76"))
                .build();

        when(expenseRepository.sumAmountByUserAndPeriod(
                userId,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 21)))
                .thenReturn(new BigDecimal("1003.74"));

        when(taskRepository.countByUserIdAndStatus(userId, TaskStatus.TO_DO))
                .thenReturn(8L);

        when(taskRepository.findDashboardTasksByUserId(userId))
                .thenReturn(List.of(
                        task1,
                        task2,
                        task3,
                        task4,
                        task5,
                        task6));

        when(taskMapper.toTaskResponseDto(task1)).thenReturn(taskDto1);
        when(taskMapper.toTaskResponseDto(task2)).thenReturn(taskDto2);
        when(taskMapper.toTaskResponseDto(task3)).thenReturn(taskDto3);
        when(taskMapper.toTaskResponseDto(task4)).thenReturn(taskDto4);
        when(taskMapper.toTaskResponseDto(task5)).thenReturn(taskDto5);

        when(inventoryItemRepository.findShoppingListByUserId(userId))
                .thenReturn(List.of(
                        item1,
                        item2,
                        item3,
                        item4,
                        item5,
                        item6));

        when(inventoryItemMapper.toInventoryItemResponseDto(item1)).thenReturn(itemDto1);
        when(inventoryItemMapper.toInventoryItemResponseDto(item2)).thenReturn(itemDto2);
        when(inventoryItemMapper.toInventoryItemResponseDto(item3)).thenReturn(itemDto3);
        when(inventoryItemMapper.toInventoryItemResponseDto(item4)).thenReturn(itemDto4);
        when(inventoryItemMapper.toInventoryItemResponseDto(item5)).thenReturn(itemDto5);

        when(expenseRepository.findTop10ByUserIdOrderByExpenseDateDescIdDesc(userId))
                .thenReturn(List.of(expense1, expense2));

        when(expenseMapper.toExpenseResponseDto(expense1)).thenReturn(expenseDto1);
        when(expenseMapper.toExpenseResponseDto(expense2)).thenReturn(expenseDto2);

        when(budgetRepository.findByScopeAndUserId(BudgetScope.GLOBAL, userId))
                .thenReturn(Optional.of(globalBudget));

        when(globalBudget.getId()).thenReturn(10);

        when(budgetService.getBudgetConsumption(10, userId))
                .thenReturn(budgetConsumption);

        DashboardResponseDto result = dashboardService.getDashboard(userId);

        assertEquals(new BigDecimal("1003.74"), result.getCurrentMonthExpensesTotal());
        assertEquals(8L, result.getTotalTasksToDo());
        assertEquals(6L, result.getTotalShoppingItems());

        assertSame(budgetConsumption, result.getGlobalBudgetConsumption());

        assertEquals(5, result.getTasksToDo().size());
        assertSame(taskDto1, result.getTasksToDo().get(0));
        assertSame(taskDto5, result.getTasksToDo().get(4));

        assertEquals(5, result.getShoppingItems().size());
        assertSame(itemDto1, result.getShoppingItems().get(0));
        assertSame(itemDto5, result.getShoppingItems().get(4));

        assertEquals(2, result.getRecentExpenses().size());
        assertSame(expenseDto1, result.getRecentExpenses().get(0));
        assertSame(expenseDto2, result.getRecentExpenses().get(1));

        verify(taskMapper, never()).toTaskResponseDto(task6);
        verify(inventoryItemMapper, never()).toInventoryItemResponseDto(item6);
    }

    @Test
    void getDashboard_shouldReturnNullBudgetAndEmptyListsWhenNoDataExists() {

        Integer userId = 1;

        when(expenseRepository.sumAmountByUserAndPeriod(
                userId,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 21)))
                .thenReturn(BigDecimal.ZERO);

        when(taskRepository.countByUserIdAndStatus(userId, TaskStatus.TO_DO))
                .thenReturn(0L);

        when(taskRepository.findDashboardTasksByUserId(userId))
                .thenReturn(List.of());

        when(inventoryItemRepository.findShoppingListByUserId(userId))
                .thenReturn(List.of());

        when(expenseRepository.findTop10ByUserIdOrderByExpenseDateDescIdDesc(userId))
                .thenReturn(List.of());

        when(budgetRepository.findByScopeAndUserId(BudgetScope.GLOBAL, userId))
                .thenReturn(Optional.empty());

        DashboardResponseDto result = dashboardService.getDashboard(userId);

        assertEquals(BigDecimal.ZERO, result.getCurrentMonthExpensesTotal());
        assertEquals(0L, result.getTotalTasksToDo());
        assertEquals(0L, result.getTotalShoppingItems());

        assertNull(result.getGlobalBudgetConsumption());

        assertEquals(0, result.getTasksToDo().size());
        assertEquals(0, result.getShoppingItems().size());
        assertEquals(0, result.getRecentExpenses().size());

        verify(budgetService, never())
                .getBudgetConsumption(
                        org.mockito.ArgumentMatchers.anyInt(),
                        org.mockito.ArgumentMatchers.anyInt());
    }
}