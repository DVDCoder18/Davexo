package com.davexo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.request.ExpenseRequestDto;
import com.davexo.backend.dto.response.ExpenseResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(
        name = "Expenses",
        description = "Expense management")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/{expenseId}")
    @Operation(summary = "Get expense", description = "Returns an expense owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Expense returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Expense not found")
    public ResponseEntity<ExpenseResponseDto> getExpenseDetail(
            @PathVariable Integer expenseId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseResponseDto response = expenseService.getExpenseDetail(expenseId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all expenses", description = "Returns all expenses owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Expenses returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<List<ExpenseResponseDto>> getAllExpenses(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<ExpenseResponseDto> response = expenseService.getAllExpenses(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create expense", description = "Creates a new expense")
    @ApiResponse(responseCode = "201", description = "Expense created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Expense category not found")
    public ResponseEntity<ExpenseResponseDto> addExpense(@Valid @RequestBody ExpenseRequestDto expenseRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetail) {

        ExpenseResponseDto response = expenseService.addExpense(expenseRequestDto, customUserDetail.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{expenseId}")
    @Operation(summary = "Update expense", description = "Updates an expense owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Expense updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Expense or expense category not found")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@Valid @RequestBody ExpenseRequestDto expenseRequestDto,
            @PathVariable Integer expenseId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseResponseDto response = expenseService.updateExpense(expenseRequestDto, expenseId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete expense", description = "Deletes an expense owned by the authenticated user")
    @ApiResponse(responseCode = "204", description = "Expense deleted successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Expense not found")
    public ResponseEntity<Void> deleteExpense(@PathVariable Integer expenseId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        expenseService.deleteExpense(expenseId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}

