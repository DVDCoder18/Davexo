package com.davexo.backend.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.davexo.backend.entity.Budget;
import com.davexo.backend.entity.Expense;
import com.davexo.backend.entity.ExpenseCategory;
import com.davexo.backend.entity.InventoryCategory;
import com.davexo.backend.entity.InventoryItem;
import com.davexo.backend.entity.Task;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.BudgetScope;
import com.davexo.backend.enums.InventoryItemType;
import com.davexo.backend.enums.InventoryStatus;
import com.davexo.backend.enums.Role;
import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.enums.TaskStatus;
import com.davexo.backend.integration.AbstractIntegrationTest;
import com.davexo.backend.repository.BudgetRepository;
import com.davexo.backend.repository.ExpenseCategoryRepository;
import com.davexo.backend.repository.ExpenseRepository;
import com.davexo.backend.repository.InventoryCategoryRepository;
import com.davexo.backend.repository.InventoryItemRepository;
import com.davexo.backend.repository.TaskRepository;
import com.davexo.backend.repository.UserRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Transactional
class DashboardApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private InventoryCategoryRepository inventoryCategoryRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void dashboard_shouldReturnAuthenticatedUserData() throws Exception {

        LocalDate today = LocalDate.now();

        User user = createUser(
                "dashboard@test.com",
                "DashboardUser",
                "Password123!");

        Budget globalBudget = Budget.builder()
                .name("Global budget")
                .amount(new BigDecimal("500.00"))
                .scope(BudgetScope.GLOBAL)
                .user(user)
                .build();

        budgetRepository.save(globalBudget);

        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .name("Food")
                .user(user)
                .build();

        expenseCategoryRepository.save(expenseCategory);

        Expense expense = Expense.builder()
                .label("Groceries")
                .amount(new BigDecimal("120.50"))
                .expenseDate(today)
                .expenseCategory(expenseCategory)
                .user(user)
                .build();

        expenseRepository.save(expense);

        Task task = Task.builder()
                .title("Important task")
                .priority(TaskPriority.URGENT)
                .status(TaskStatus.TO_DO)
                .createdAt(today.minusDays(1))
                .dueDate(today.plusDays(1))
                .user(user)
                .build();

        taskRepository.save(task);

        InventoryCategory inventoryCategory = InventoryCategory.builder()
                .name("Food")
                .user(user)
                .build();

        inventoryCategoryRepository.save(inventoryCategory);

        InventoryItem inventoryItem = InventoryItem.builder()
                .name("Coffee")
                .type(InventoryItemType.CONSUMABLE)
                .status(InventoryStatus.MISSING)
                .inventoryCategory(inventoryCategory)
                .user(user)
                .build();

        inventoryItemRepository.save(inventoryItem);

        String token = loginAndGetToken(
                "dashboard@test.com",
                "Password123!");

        mockMvc.perform(
                get("/api/dashboard")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentMonthExpensesTotal").value(120.50))
                .andExpect(jsonPath("$.totalTasksToDo").value(1))
                .andExpect(jsonPath("$.totalShoppingItems").value(1))
                .andExpect(jsonPath("$.globalBudgetConsumption").exists())
                .andExpect(jsonPath("$.globalBudgetConsumption.budgetAmount").value(500.00))
                .andExpect(jsonPath("$.globalBudgetConsumption.spentAmount").value(120.50))
                .andExpect(jsonPath("$.globalBudgetConsumption.remainingAmount").value(379.50))
                .andExpect(jsonPath("$.tasksToDo.length()").value(1))
                .andExpect(jsonPath("$.tasksToDo[0].title").value("Important task"))
                .andExpect(jsonPath("$.shoppingItems.length()").value(1))
                .andExpect(jsonPath("$.shoppingItems[0].name").value("Coffee"))
                .andExpect(jsonPath("$.recentExpenses.length()").value(1))
                .andExpect(jsonPath("$.recentExpenses[0].label").value("Groceries"));
    }

    @Test
    void dashboard_shouldReturnUnauthorizedWithoutToken() throws Exception {

        mockMvc.perform(
                get("/api/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    private User createUser(
            String email,
            String pseudo,
            String plainPassword) {

        User user = User.builder()
                .pseudo(pseudo)
                .email(email)
                .passwordHash(passwordEncoder.encode(plainPassword))
                .createdAt(LocalDate.of(2026, 1, 1))
                .role(Role.USER)
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    private String loginAndGetToken(
            String email,
            String password) throws Exception {

        String loginRequest = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        MvcResult loginResult = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode responseJson = jsonMapper.readTree(
                loginResult.getResponse().getContentAsString());

        return responseJson
                .get("token")
                .asString();
    }
}