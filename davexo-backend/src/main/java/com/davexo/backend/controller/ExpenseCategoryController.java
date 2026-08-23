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

import com.davexo.backend.dto.request.ExpenseCategoryRequestDto;
import com.davexo.backend.dto.response.ExpenseCategoryResponseDto;
import com.davexo.backend.exception.CustomErrorResponse;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.ExpenseCategoryService;

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
@RequestMapping("/api/expense-categories")
@RequiredArgsConstructor
@Tag(
        name = "Expense Categories",
        description = "Expense category management")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    @GetMapping(
            value = "/{expenseCategoryId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get expense category")
    @ApiResponse(
            responseCode = "200",
            description = "Expense category returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExpenseCategoryResponseDto.class)))
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
    public ResponseEntity<ExpenseCategoryResponseDto> getExpenseCategoryDetail(
            @PathVariable("expenseCategoryId") Integer expenseCategoryId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseCategoryResponseDto response =
                expenseCategoryService.getExpenseCategoryDetail(
                        expenseCategoryId,
                        customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all expense categories")
    @ApiResponse(
            responseCode = "200",
            description = "Expense categories returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = ExpenseCategoryResponseDto.class))))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<List<ExpenseCategoryResponseDto>> getAllExpenseCategories(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                expenseCategoryService.getAllExpenseCategories(
                        customUserDetails.getUser().getId()));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create expense category")
    @ApiResponse(
            responseCode = "201",
            description = "Expense category created successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            implementation = ExpenseCategoryResponseDto.class)))
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
            description = "Budget not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Expense category business rule conflict",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<ExpenseCategoryResponseDto> addExpenseCategory(
            @Valid @RequestBody ExpenseCategoryRequestDto expenseCategoryRequestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseCategoryResponseDto response =
                expenseCategoryService.addExpenseCategory(
                        expenseCategoryRequestDto,
                        customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(
            value = "/{expenseCategoryId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update expense category")
    @ApiResponse(
            responseCode = "200",
            description = "Expense category updated successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            implementation = ExpenseCategoryResponseDto.class)))
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
            description = "Expense category or budget not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Expense category business rule conflict",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<ExpenseCategoryResponseDto> updateExpenseCategory(
            @Valid @RequestBody ExpenseCategoryRequestDto expenseCategoryRequestDto,
            @PathVariable("expenseCategoryId") Integer expenseCategoryId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ExpenseCategoryResponseDto response =
                expenseCategoryService.updateExpenseCategory(
                        expenseCategoryRequestDto,
                        expenseCategoryId,
                        customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{expenseCategoryId}")
    @Operation(summary = "Delete expense category")
    @ApiResponse(
            responseCode = "204",
            description = "Expense category deleted successfully",
            content = @Content)
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
    public ResponseEntity<Void> deleteExpenseCategory(
            @PathVariable("expenseCategoryId") Integer expenseCategoryId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        expenseCategoryService.deleteExpenseCategory(
                expenseCategoryId,
                customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}