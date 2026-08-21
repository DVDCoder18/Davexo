package com.davexo.backend.service;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.request.InventoryItemRequestDto;
import com.davexo.backend.dto.response.InventoryItemResponseDto;
import com.davexo.backend.entity.InventoryCategory;
import com.davexo.backend.entity.InventoryItem;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.InventoryItemType;
import com.davexo.backend.enums.InventoryStatus;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.InventoryItemMapper;
import com.davexo.backend.repository.InventoryCategoryRepository;
import com.davexo.backend.repository.InventoryItemRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryCategoryRepository inventoryCategoryRepository;
    private final InventoryItemMapper inventoryItemMapper;

    private boolean isStatusValidForType(InventoryItemType type, InventoryStatus status) {

        List<InventoryStatus> consumableStatuses = List.of(
                InventoryStatus.IN_STOCK,
                InventoryStatus.LOW_STOCK,
                InventoryStatus.MISSING);

        List<InventoryStatus> durableStatuses = List.of(
                InventoryStatus.IN_SERVICE,
                InventoryStatus.OUT_OF_SERVICE,
                InventoryStatus.TO_REPLACE,
                InventoryStatus.NEED_MORE);

        if (type == InventoryItemType.CONSUMABLE) {
            return consumableStatuses.contains(status);
        }

        if (type == InventoryItemType.DURABLE) {
            return durableStatuses.contains(status);
        }

        return false;
    }

    @Transactional
    public InventoryItemResponseDto addInventoryItem(InventoryItemRequestDto inventoryItemRequestDto,
            User authenticatedUser) {

        if (!isStatusValidForType(inventoryItemRequestDto.getType(), inventoryItemRequestDto.getStatus())) {
            throw new BusinessException("The inventory item type and inventory status combination is invalid");
        }

        InventoryItem inventoryItem = inventoryItemMapper.toInventoryItemEntity(inventoryItemRequestDto);

        inventoryItem.setUser(authenticatedUser);

        inventoryItem.setInventoryCategory(
                inventoryCategoryRepository.findByIdAndUserId(
                        inventoryItemRequestDto.getInventoryCategoryId(), authenticatedUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory category not found")));

        InventoryItem savedInventoryItem = inventoryItemRepository.save(inventoryItem);

        return inventoryItemMapper.toInventoryItemResponseDto(savedInventoryItem);
    }

    @Transactional
    public InventoryItemResponseDto updateInventoryItem(InventoryItemRequestDto inventoryItemRequestDto,
            Integer inventoryItemId, Integer userId) {

        if (!isStatusValidForType(inventoryItemRequestDto.getType(), inventoryItemRequestDto.getStatus())) {
            throw new BusinessException("The inventory item type and inventory status combination is invalid");
        }

        InventoryItem inventoryItem = inventoryItemRepository.findByIdAndUserId(inventoryItemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));

        InventoryCategory inventoryCategory = inventoryCategoryRepository
                .findByIdAndUserId(inventoryItemRequestDto.getInventoryCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item category not found"));

        inventoryItem.setName(inventoryItemRequestDto.getName());
        inventoryItem.setType(inventoryItemRequestDto.getType());
        inventoryItem.setStatus(inventoryItemRequestDto.getStatus());
        inventoryItem.setNote(inventoryItemRequestDto.getNote());
        inventoryItem.setInventoryCategory(inventoryCategory);

        InventoryItem savedInventoryItem = inventoryItemRepository.save(inventoryItem);

        return inventoryItemMapper.toInventoryItemResponseDto(savedInventoryItem);
    }
    
    @Transactional
    public void deleteInventoryItem(Integer inventoryItemId, Integer userId) {
        long deleteCount = inventoryItemRepository.deleteByIdAndUserId(inventoryItemId, userId);

        if (deleteCount == 0) {
            throw new ResourceNotFoundException("Inventory item not found");
        }
    }
    
    @Transactional
    public InventoryItemResponseDto getInventoryItemDetail(Integer inventoryItemId, Integer userId) {
        InventoryItem inventoryItem = inventoryItemRepository.findByIdAndUserId(inventoryItemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));

        return inventoryItemMapper.toInventoryItemResponseDto(inventoryItem);
    }

    @Transactional
    public List<InventoryItemResponseDto> getAllInventoryItems(Integer userId) {
        
        List<InventoryItem> inventoryItemList = inventoryItemRepository.findAllByUserId(userId);

        return inventoryItemList.stream()
                .map(inventoryItemMapper::toInventoryItemResponseDto)
                .toList();
    }
    
}
