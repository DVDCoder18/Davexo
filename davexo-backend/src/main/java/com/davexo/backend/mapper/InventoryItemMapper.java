package com.davexo.backend.mapper;

import org.springframework.stereotype.Component;

import com.davexo.backend.dto.request.InventoryItemRequestDto;
import com.davexo.backend.dto.response.InventoryItemResponseDto;
import com.davexo.backend.entity.InventoryItem;

@Component
public class InventoryItemMapper {

    public InventoryItemResponseDto toInventoryItemResponseDto(InventoryItem inventoryItem) {
        return InventoryItemResponseDto.builder()
                .id(inventoryItem.getId())
                .name(inventoryItem.getName())
                .type(inventoryItem.getType())
                .status(inventoryItem.getStatus())
                .note(inventoryItem.getNote())
                .inventoryCategoryId(inventoryItem.getInventoryCategory().getId())
                .inventoryCategoryName(inventoryItem.getInventoryCategory().getName())
                .build();
    }

    public InventoryItem toInventoryItemEntity(InventoryItemRequestDto inventoryItemRequestDto) {
        return InventoryItem.builder()
                .name(inventoryItemRequestDto.getName())
                .type(inventoryItemRequestDto.getType())
                .status(inventoryItemRequestDto.getStatus())
                .note(inventoryItemRequestDto.getNote())
                .build();
    }
}
