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

import com.davexo.backend.dto.request.ExpenseCategoryRequestDto;
import com.davexo.backend.dto.response.ExpenseCategoryResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.ExpenseCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/expense-categories")
@RequiredArgsConstructor
@Tag(
        name = "Expense Categories",
        description = "Expense category management")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;


    @GetMapping("/{expenseCategoryId}")
    @Operation(summary = "Get expense category")
    @ApiResponse(responseCode = "200", description = "Expense category returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Expense category not found")
    public ResponseEntity<ExpenseCategoryResponseDto> getExpenseCategoryDetail(@PathVariable Integer expenseCategoryId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseCategoryResponseDto response = expenseCategoryService.getExpenseCategoryDetail(expenseCategoryId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all expense categories")
    @ApiResponse(responseCode = "200", description = "Expense categories returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<List<ExpenseCategoryResponseDto>> getAllExpenseCategories(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        List<ExpenseCategoryResponseDto> response = expenseCategoryService
                .getAllExpenseCategories(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create expense category")
    @ApiResponse(responseCode = "201", description = "Expense category created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<ExpenseCategoryResponseDto> addExpenseCategory(
            @Valid @RequestBody ExpenseCategoryRequestDto expenseCategoryRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseCategoryResponseDto response = expenseCategoryService
                .addExpenseCategory(expenseCategoryRequestDto, customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{expenseCategoryId}")
    @Operation(summary = "Update expense category")
    @ApiResponse(responseCode = "200", description = "Expense category updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Expense category not found")
    public ResponseEntity<ExpenseCategoryResponseDto> updateExpenseCategory(
            @Valid @RequestBody ExpenseCategoryRequestDto expenseCategoryRequestDto,
            @PathVariable Integer expenseCategoryId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseCategoryResponseDto response = expenseCategoryService.updateExpenseCategory(expenseCategoryRequestDto,
                expenseCategoryId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{expenseCategoryId}")
    @Operation(summary = "Delete expense category")
    @ApiResponse(responseCode = "204", description = "Expense category deleted successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Expense category not found")
    public ResponseEntity<Void> deleteExpenseCategory(@PathVariable Integer expenseCategoryId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        expenseCategoryService.deleteExpenseCategory(expenseCategoryId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}
