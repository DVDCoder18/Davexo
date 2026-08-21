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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory-items")
@RequiredArgsConstructor
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @GetMapping("/{inventoryItemId}")
    public ResponseEntity<InventoryItemResponseDto> getInventoryItemDetail(@PathVariable Integer inventoryItemId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryItemResponseDto response = inventoryItemService.getInventoryItemDetail(inventoryItemId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<InventoryItemResponseDto>> getAllInventoryItems(
                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {

            List<InventoryItemResponseDto> response = inventoryItemService
                            .getAllInventoryItems(customUserDetails.getUser().getId());

            return ResponseEntity.ok(response);
    }
    
    @GetMapping("/shopping-list")
    public ResponseEntity<ShoppingListResponseDto> getShoppingList(
                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
            ShoppingListResponseDto response = inventoryItemService.getShoppingList(customUserDetails.getUser().getId());

            return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<InventoryItemResponseDto> addInventoryItem(
            @Valid @RequestBody InventoryItemRequestDto inventoryItemRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryItemResponseDto response = inventoryItemService.addInventoryItem(inventoryItemRequestDto,
                customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{inventoryItemId}")
    public ResponseEntity<InventoryItemResponseDto> updateInventoryItem(
            @Valid @RequestBody InventoryItemRequestDto inventoryItemRequestDto, @PathVariable Integer inventoryItemId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryItemResponseDto response = inventoryItemService.updateInventoryItem(inventoryItemRequestDto,
                inventoryItemId, customUserDetails.getUser().getId());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{inventoryItemId}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Integer inventoryItemId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        inventoryItemService.deleteInventoryItem(inventoryItemId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}
