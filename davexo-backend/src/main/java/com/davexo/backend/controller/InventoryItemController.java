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

import com.davexo.backend.dto.request.InventoryItemRequestDto;
import com.davexo.backend.dto.response.InventoryItemResponseDto;
import com.davexo.backend.dto.response.ShoppingListResponseDto;
import com.davexo.backend.exception.CustomErrorResponse;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.InventoryItemService;

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
@RequestMapping("/api/inventory-items")
@RequiredArgsConstructor
@Tag(
        name = "Inventory Items",
        description = "Inventory item management and shopping list")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @GetMapping(
            value = "/{inventoryItemId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get inventory item",
            description = "Returns an inventory item owned by the authenticated user")
    @ApiResponse(
            responseCode = "200",
            description = "Inventory item returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            implementation = InventoryItemResponseDto.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Inventory item not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<InventoryItemResponseDto> getInventoryItemDetail(
            @PathVariable("inventoryItemId") Integer inventoryItemId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                inventoryItemService.getInventoryItemDetail(
                        inventoryItemId,
                        customUserDetails.getUser().getId()));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all inventory items")
    @ApiResponse(
            responseCode = "200",
            description = "Inventory items returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = InventoryItemResponseDto.class))))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<List<InventoryItemResponseDto>> getAllInventoryItems(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                inventoryItemService.getAllInventoryItems(
                        customUserDetails.getUser().getId()));
    }

    @GetMapping(
            value = "/shopping-list",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get shopping list",
            description = "Returns inventory items that currently require purchase or replacement, grouped by priority")
    @ApiResponse(
            responseCode = "200",
            description = "Shopping list returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            implementation = ShoppingListResponseDto.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<ShoppingListResponseDto> getShoppingList(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                inventoryItemService.getShoppingList(
                        customUserDetails.getUser().getId()));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create inventory item")
    @ApiResponse(
            responseCode = "201",
            description = "Inventory item created successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            implementation = InventoryItemResponseDto.class)))
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
            description = "Inventory category not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Inventory item business rule conflict",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<InventoryItemResponseDto> addInventoryItem(
            @Valid @RequestBody InventoryItemRequestDto inventoryItemRequestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        InventoryItemResponseDto response =
                inventoryItemService.addInventoryItem(
                        inventoryItemRequestDto,
                        customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(
            value = "/{inventoryItemId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update inventory item")
    @ApiResponse(
            responseCode = "200",
            description = "Inventory item updated successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            implementation = InventoryItemResponseDto.class)))
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
            description = "Inventory item or inventory category not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Inventory item business rule conflict",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<InventoryItemResponseDto> updateInventoryItem(
            @Valid @RequestBody InventoryItemRequestDto inventoryItemRequestDto,
            @PathVariable("inventoryItemId") Integer inventoryItemId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                inventoryItemService.updateInventoryItem(
                        inventoryItemRequestDto,
                        inventoryItemId,
                        customUserDetails.getUser().getId()));
    }

    @DeleteMapping("/{inventoryItemId}")
    @Operation(summary = "Delete inventory item")
    @ApiResponse(
            responseCode = "204",
            description = "Inventory item deleted successfully",
            content = @Content)
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Inventory item not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<Void> deleteInventoryItem(
            @PathVariable("inventoryItemId") Integer inventoryItemId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        inventoryItemService.deleteInventoryItem(
                inventoryItemId,
                customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}