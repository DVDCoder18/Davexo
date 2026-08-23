package com.davexo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.davexo.backend.exception.CustomErrorResponse;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense management")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping(
            value = "/{expenseId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get expense",
            description = "Returns an expense owned by the authenticated user")
    @ApiResponse(
            responseCode = "200",
            description = "Expense returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExpenseResponseDto.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Expense not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<ExpenseResponseDto> getExpenseDetail(
            @PathVariable("expenseId") Integer expenseId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                expenseService.getExpenseDetail(
                        expenseId,
                        customUserDetails.getUser().getId()));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all expenses",
            description = "Returns all expenses owned by the authenticated user")
    @ApiResponse(
            responseCode = "200",
            description = "Expenses returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = ExpenseResponseDto.class))))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<List<ExpenseResponseDto>> getAllExpenses(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                expenseService.getAllExpenses(customUserDetails.getUser().getId()));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create expense",
            description = "Creates a new expense")
    @ApiResponse(
            responseCode = "201",
            description = "Expense created successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExpenseResponseDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Expense category not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<ExpenseResponseDto> addExpense(
            @Valid @RequestBody ExpenseRequestDto expenseRequestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseResponseDto response = expenseService.addExpense(
                expenseRequestDto,
                customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(
            value = "/{expenseId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update expense",
            description = "Updates an expense owned by the authenticated user")
    @ApiResponse(
            responseCode = "200",
            description = "Expense updated successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExpenseResponseDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Expense or expense category not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<ExpenseResponseDto> updateExpense(
            @Valid @RequestBody ExpenseRequestDto expenseRequestDto,
            @PathVariable("expenseId") Integer expenseId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                expenseService.updateExpense(
                        expenseRequestDto,
                        expenseId,
                        customUserDetails.getUser().getId()));
    }

    @DeleteMapping("/{expenseId}")
    @Operation(
            summary = "Delete expense",
            description = "Deletes an expense owned by the authenticated user")
    @ApiResponse(
            responseCode = "204",
            description = "Expense deleted successfully",
            content = @Content)
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Expense not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<Void> deleteExpense(
            @PathVariable("expenseId") Integer expenseId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        expenseService.deleteExpense(
                expenseId,
                customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}