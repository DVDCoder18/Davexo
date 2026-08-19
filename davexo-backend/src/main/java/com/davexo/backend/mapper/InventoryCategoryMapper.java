package com.davexo.backend.mapper;

import org.springframework.stereotype.Component;

import com.davexo.backend.dto.request.InventoryCategoryRequestDto;
import com.davexo.backend.dto.response.InventoryCategoryResponseDto;
import com.davexo.backend.entity.InventoryCategory;

@Component
public class InventoryCategoryMapper {

    public InventoryCategoryResponseDto toInventoryCategoryResponseDto(InventoryCategory inventoryCategory) {
        return InventoryCategoryResponseDto.builder()
                .id(inventoryCategory.getId())
                .name(inventoryCategory.getName())
                .build();
    }

    public InventoryCategory toInventoryCategoryEntity(InventoryCategoryRequestDto inventoryCategoryRequestDto) {
        return InventoryCategory.builder()
            .name(inventoryCategoryRequestDto.getName())
            .build();
    }
}
