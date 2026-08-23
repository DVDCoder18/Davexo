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

import com.davexo.backend.dto.request.BudgetRequestDto;
import com.davexo.backend.dto.response.BudgetConsumptionResponseDto;
import com.davexo.backend.dto.response.BudgetResponseDto;
import com.davexo.backend.exception.CustomErrorResponse;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.BudgetService;

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
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(
        name = "Budgets",
        description = "Budget management and budget consumption")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping(
            value = "/{budgetId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get budget",
            description = "Returns the details of a budget owned by the authenticated user")
    @ApiResponse(
            responseCode = "200",
            description = "Budget returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BudgetResponseDto.class)))
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
    public ResponseEntity<BudgetResponseDto> getBudgetDetail(
            @PathVariable("budgetId") Integer budgetId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetResponseDto response = budgetService.getBudgetDetail(
                budgetId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all budgets",
            description = "Returns all budgets owned by the authenticated user")
    @ApiResponse(
            responseCode = "200",
            description = "Budgets returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = BudgetResponseDto.class))))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<List<BudgetResponseDto>> getAllBudgets(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                budgetService.getAllBudgets(customUserDetails.getUser().getId()));
    }

    @GetMapping(
            value = "/{budgetId}/consumption",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get budget consumption",
            description = "Returns the current month consumption of a budget")
    @ApiResponse(
            responseCode = "200",
            description = "Budget consumption returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            implementation = BudgetConsumptionResponseDto.class)))
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
    public ResponseEntity<BudgetConsumptionResponseDto> getBudgetConsumption(
            @PathVariable("budgetId") Integer budgetId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetConsumptionResponseDto response =
                budgetService.getBudgetConsumption(
                        budgetId,
                        customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/consumptions",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all budget consumptions",
            description = "Returns current month consumption data for all budgets owned by the authenticated user")
    @ApiResponse(
            responseCode = "200",
            description = "Budget consumptions returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = BudgetConsumptionResponseDto.class))))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<List<BudgetConsumptionResponseDto>> getAllBudgetConsumptions(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                budgetService.getAllBudgetConsumptions(
                        customUserDetails.getUser().getId()));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create budget",
            description = "Creates a global budget or a budget associated with selected expense categories")
    @ApiResponse(
            responseCode = "201",
            description = "Budget created successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BudgetResponseDto.class)))
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
            description = "One or more expense categories were not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Budget business rule conflict",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<BudgetResponseDto> createBudget(
            @Valid @RequestBody BudgetRequestDto budgetRequestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetResponseDto response = budgetService.createBudget(
                budgetRequestDto,
                customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(
            value = "/{budgetId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update budget",
            description = "Updates a budget owned by the authenticated user")
    @ApiResponse(
            responseCode = "200",
            description = "Budget updated successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BudgetResponseDto.class)))
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
            description = "Budget or expense category not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Budget business rule conflict",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<BudgetResponseDto> updateBudget(
            @Valid @RequestBody BudgetRequestDto budgetRequestDto,
            @PathVariable("budgetId") Integer budgetId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BudgetResponseDto response = budgetService.updateBudget(
                budgetRequestDto,
                budgetId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{budgetId}")
    @Operation(
            summary = "Delete budget",
            description = "Deletes a budget owned by the authenticated user")
    @ApiResponse(
            responseCode = "204",
            description = "Budget deleted successfully",
            content = @Content)
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
    public ResponseEntity<Void> deleteBudget(
            @PathVariable("budgetId") Integer budgetId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        budgetService.deleteBudget(
                budgetId,
                customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}