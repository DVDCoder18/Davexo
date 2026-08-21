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

import com.davexo.backend.dto.request.BudgetRequestDto;
import com.davexo.backend.dto.response.BudgetConsumptionResponseDto;
import com.davexo.backend.dto.response.BudgetResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.BudgetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(
        name = "Budgets",
        description = "Budget management and budget consumption")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/{budgetId}")
    @Operation(
        summary = "Get budget",
        description = "Returns the details of a budget owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Budget returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Budget not found")
    public ResponseEntity<BudgetResponseDto> getBudgetDetail(@PathVariable Integer budgetId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        BudgetResponseDto response = budgetService.getBudgetDetail(budgetId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all budgets", description = "Returns all budgets owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Budgets returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<List<BudgetResponseDto>> getAllBudgets(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<BudgetResponseDto> response = budgetService.getAllBudgets(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{budgetId}/consumption")
    @Operation(summary = "Get budget consumption", description = "Returns the current month consumption of a budget")
    @ApiResponse(responseCode = "200", description = "Budget consumption returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Budget not found")
    public ResponseEntity<BudgetConsumptionResponseDto> getBudgetConsumption(
            @PathVariable Integer budgetId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetConsumptionResponseDto response = budgetService.getBudgetConsumption(
                budgetId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/consumptions")
    @Operation(summary = "Get all budget consumptions", description = "Returns current month consumption data for all budgets owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Budget consumptions returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<List<BudgetConsumptionResponseDto>> getAllBudgetConsumptions(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<BudgetConsumptionResponseDto> response = budgetService.getAllBudgetConsumptions(
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create budget", description = "Creates a global budget or a budget associated with selected expense categories")
    @ApiResponse(responseCode = "201", description = "Budget created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "One or more expense categories were not found")
    @ApiResponse(responseCode = "409", description = "Budget business rule conflict")
    public ResponseEntity<BudgetResponseDto> createBudget(@Valid @RequestBody BudgetRequestDto budgetRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetResponseDto response = budgetService.createBudget(budgetRequestDto, customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{budgetId}")
    @Operation(summary = "Update budget", description = "Updates a budget owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Budget updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Budget or expense category not found")
    @ApiResponse(responseCode = "409", description = "Budget business rule conflict")
    public ResponseEntity<BudgetResponseDto> updateBudget(@Valid @RequestBody BudgetRequestDto budgetRequestDto,
            @PathVariable Integer budgetId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetResponseDto response = budgetService.updateBudget(budgetRequestDto, budgetId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{budgetId}")
    @Operation(summary = "Delete budget", description = "Deletes a budget owned by the authenticated user")
    @ApiResponse(responseCode = "204", description = "Budget deleted successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Budget not found")
    public ResponseEntity<Void> deleteBudget(@PathVariable Integer budgetId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        budgetService.deleteBudget(budgetId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }

}
