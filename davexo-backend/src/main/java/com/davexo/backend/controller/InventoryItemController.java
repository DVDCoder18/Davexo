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

import com.davexo.backend.dto.request.InventoryItemRequestDto;
import com.davexo.backend.dto.response.InventoryItemResponseDto;
import com.davexo.backend.dto.response.ShoppingListResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.InventoryItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory-items")
@RequiredArgsConstructor
@Tag(
        name = "Inventory Items",
        description = "Inventory item management and shopping list")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @GetMapping("/{inventoryItemId}")
    @Operation(summary = "Get inventory item", description = "Returns an inventory item owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Inventory item returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<InventoryItemResponseDto> getInventoryItemDetail(@PathVariable Integer inventoryItemId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryItemResponseDto response = inventoryItemService.getInventoryItemDetail(inventoryItemId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all inventory items")
    @ApiResponse(responseCode = "200", description = "Inventory items returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<List<InventoryItemResponseDto>> getAllInventoryItems(
                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {

            List<InventoryItemResponseDto> response = inventoryItemService
                            .getAllInventoryItems(customUserDetails.getUser().getId());

            return ResponseEntity.ok(response);
    }
    
    @GetMapping("/shopping-list")
    @Operation(summary = "Get shopping list", description = "Returns inventory items that currently require purchase or replacement, grouped by priority")
    @ApiResponse(responseCode = "200", description = "Shopping list returned successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<ShoppingListResponseDto> getShoppingList(
                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
            ShoppingListResponseDto response = inventoryItemService.getShoppingList(customUserDetails.getUser().getId());

            return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create inventory item")
    @ApiResponse(responseCode = "201", description = "Inventory item created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Inventory category not found")
    public ResponseEntity<InventoryItemResponseDto> addInventoryItem(
            @Valid @RequestBody InventoryItemRequestDto inventoryItemRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryItemResponseDto response = inventoryItemService.addInventoryItem(inventoryItemRequestDto,
                customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{inventoryItemId}")
    @Operation(summary = "Update inventory item")
    @ApiResponse(responseCode = "200", description = "Inventory item updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Inventory item or inventory category not found")
    public ResponseEntity<InventoryItemResponseDto> updateInventoryItem(
            @Valid @RequestBody InventoryItemRequestDto inventoryItemRequestDto, @PathVariable Integer inventoryItemId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryItemResponseDto response = inventoryItemService.updateInventoryItem(inventoryItemRequestDto,
                inventoryItemId, customUserDetails.getUser().getId());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{inventoryItemId}")
    @Operation(summary = "Delete inventory item")
    @ApiResponse(responseCode = "204", description = "Inventory item deleted successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Inventory item not found")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Integer inventoryItemId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        inventoryItemService.deleteInventoryItem(inventoryItemId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}
