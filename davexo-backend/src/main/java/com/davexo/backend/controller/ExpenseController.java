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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDto> getExpenseDetail(
            @PathVariable Integer expenseId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseResponseDto response = expenseService.getExpenseDetail(expenseId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> getAllExpenses(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<ExpenseResponseDto> response = expenseService.getAllExpenses(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> addExpense(@Valid @RequestBody ExpenseRequestDto expenseRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetail) {

        ExpenseResponseDto response = expenseService.addExpense(expenseRequestDto, customUserDetail.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@Valid @RequestBody ExpenseRequestDto expenseRequestDto,
            @PathVariable Integer expenseId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseResponseDto response = expenseService.updateExpense(expenseRequestDto, expenseId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Integer expenseId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        expenseService.deleteExpense(expenseId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}

