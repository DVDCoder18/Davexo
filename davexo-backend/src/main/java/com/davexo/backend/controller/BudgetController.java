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
import com.davexo.backend.dto.response.BudgetResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.BudgetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDto> getBudgetDetail(@PathVariable Integer budgetId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        BudgetResponseDto response = budgetService.getBudgetDetail(budgetId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponseDto>> getAllBudgets(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<BudgetResponseDto> response = budgetService.getAllBudgets(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BudgetResponseDto> createBudget(@Valid @RequestBody BudgetRequestDto budgetRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetResponseDto response = budgetService.createBudget(budgetRequestDto, customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDto> updateBudget(@Valid @RequestBody BudgetRequestDto budgetRequestDto,
            @PathVariable Integer budgetId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetResponseDto response = budgetService.updateBudget(budgetRequestDto, budgetId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Integer budgetId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        budgetService.deleteBudget(budgetId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }

}
