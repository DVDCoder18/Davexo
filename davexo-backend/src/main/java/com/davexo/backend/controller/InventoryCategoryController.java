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

import com.davexo.backend.dto.request.InventoryCategoryRequestDto;
import com.davexo.backend.dto.response.InventoryCategoryResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.InventoryCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory-categories")
@RequiredArgsConstructor
@Tag(
        name = "Inventory Categories",
        description = "Inventory category management")
public class InventoryCategoryController {

    private final InventoryCategoryService inventoryCategoryService;

    @GetMapping("/{inventoryCategoryId}")
    @Operation(summary = "Get inventory category")
    @ApiResponse(responseCode = "200", description = "Inventory category returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Inventory category not found")
    public ResponseEntity<InventoryCategoryResponseDto> getInventoryCategoryDetail(
            @PathVariable Integer inventoryCategoryId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryCategoryResponseDto response = inventoryCategoryService
                .getInventoryCategoryDetail(inventoryCategoryId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all inventory categories")
    @ApiResponse(responseCode = "200", description = "Inventory categories returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<List<InventoryCategoryResponseDto>> getAllInventoryCategories(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<InventoryCategoryResponseDto> response = inventoryCategoryService
                .getAllInventoryCategories(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create inventory category")
    @ApiResponse(responseCode = "201", description = "Inventory category created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<InventoryCategoryResponseDto> addInventoryCategory(
            @Valid @RequestBody InventoryCategoryRequestDto inventoryCategoryRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryCategoryResponseDto response = inventoryCategoryService
                .addInventoryCategory(inventoryCategoryRequestDto, customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{inventoryCategoryId}")
    @Operation(summary = "Update inventory category")
    @ApiResponse(responseCode = "200", description = "Inventory category updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Inventory category not found")
    public ResponseEntity<InventoryCategoryResponseDto> updateInventoryCategory(
            @Valid @RequestBody InventoryCategoryRequestDto inventoryCategoryRequestDto,
            @PathVariable Integer inventoryCategoryId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryCategoryResponseDto response = inventoryCategoryService.updateInventoryCategory(
                inventoryCategoryRequestDto, inventoryCategoryId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{inventoryCategoryId}")
    @Operation(summary = "Delete inventory category")
    @ApiResponse(responseCode = "204", description = "Inventory category deleted successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Inventory category not found")
    public ResponseEntity<Void> deleteInventoryCategory(
            @PathVariable Integer inventoryCategoryId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        inventoryCategoryService.deleteInventoryCategory(inventoryCategoryId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}
