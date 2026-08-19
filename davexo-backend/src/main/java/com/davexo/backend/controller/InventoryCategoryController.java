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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory-categories")
@RequiredArgsConstructor
public class InventoryCategoryController {

    private final InventoryCategoryService inventoryCategoryService;

    @GetMapping("/{inventoryCategoryId}")
    public ResponseEntity<InventoryCategoryResponseDto> getInventoryCategoryDetail(
            @PathVariable Integer inventoryCategoryId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryCategoryResponseDto response = inventoryCategoryService
                .getInventoryCategoryDetail(inventoryCategoryId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryCategoryResponseDto>> getAllInventoryCategories(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<InventoryCategoryResponseDto> response = inventoryCategoryService
                .getAllInventoryCategories(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<InventoryCategoryResponseDto> addInventoryCategory(
            @Valid @RequestBody InventoryCategoryRequestDto inventoryCategoryRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryCategoryResponseDto response = inventoryCategoryService
                .addInventoryCategory(inventoryCategoryRequestDto, customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{inventoryCategoryId}")
    public ResponseEntity<InventoryCategoryResponseDto> updateInventoryCategory(
            @Valid @RequestBody InventoryCategoryRequestDto inventoryCategoryRequestDto,
            @PathVariable Integer inventoryCategoryId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryCategoryResponseDto response = inventoryCategoryService.updateInventoryCategory(
                inventoryCategoryRequestDto, inventoryCategoryId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{inventoryCategoryId}")
    public ResponseEntity<Void> deleteInventoryCategory(
            @PathVariable Integer inventoryCategoryId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        inventoryCategoryService.deleteInventoryCategory(inventoryCategoryId, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}
