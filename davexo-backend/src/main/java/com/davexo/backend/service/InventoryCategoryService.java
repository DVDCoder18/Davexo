package com.davexo.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.davexo.backend.dto.request.InventoryCategoryRequestDto;
import com.davexo.backend.dto.response.InventoryCategoryResponseDto;
import com.davexo.backend.entity.InventoryCategory;
import com.davexo.backend.entity.User;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.InventoryCategoryMapper;
import com.davexo.backend.repository.InventoryCategoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryCategoryService {

    private final InventoryCategoryRepository inventoryCategoryRepository;
    private final InventoryCategoryMapper inventoryCategoryMapper;


    @Transactional
    public InventoryCategoryResponseDto addInventoryCategory(InventoryCategoryRequestDto inventoryCategoryRequestDto,
            User authenticatedUser) {

        if (inventoryCategoryRepository.existsByNameIgnoreCaseAndUserId(inventoryCategoryRequestDto.getName(),
                authenticatedUser.getId())) {
            throw new BusinessException("You already possess an inventory category with this name");
        }

        InventoryCategory inventoryCategory = inventoryCategoryMapper
                .toInventoryCategoryEntity(inventoryCategoryRequestDto);

        inventoryCategory.setUser(authenticatedUser);

        InventoryCategory savedInventoryCategory = inventoryCategoryRepository.save(inventoryCategory);

        return inventoryCategoryMapper.toInventoryCategoryResponseDto(savedInventoryCategory);
    }
    

    @Transactional
    public InventoryCategoryResponseDto updateInventoryCategory(InventoryCategoryRequestDto inventoryCategoryRequestDto,
            Integer inventoryCategoryId, Integer userId) {

        InventoryCategory inventoryCategory = inventoryCategoryRepository.findByIdAndUserId(inventoryCategoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory category not found"));

        if (inventoryCategoryRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(inventoryCategoryRequestDto.getName(),
                userId, inventoryCategoryId)) {
            throw new BusinessException("You already possess an inventory category with this name");
        }

        inventoryCategory.setName(inventoryCategoryRequestDto.getName());

        InventoryCategory savedInventoryCategory = inventoryCategoryRepository.save(inventoryCategory);

        return inventoryCategoryMapper.toInventoryCategoryResponseDto(savedInventoryCategory);

    }

    @Transactional
    public void deleteInventoryCategory(Integer inventoryCategoryId, Integer userId) {
        long deleteCount = inventoryCategoryRepository.deleteByIdAndUserId(inventoryCategoryId, userId);

        if (deleteCount == 0) {
            throw new ResourceNotFoundException("Inventory category not found");
        }
    }

    @Transactional
    public InventoryCategoryResponseDto getInventoryCategoryDetail(Integer inventoryCategoryId, Integer userId) {

        InventoryCategory inventoryCategory = inventoryCategoryRepository.findByIdAndUserId(inventoryCategoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory category not found"));

        return inventoryCategoryMapper.toInventoryCategoryResponseDto(inventoryCategory);
    }

    @Transactional
    public List<InventoryCategoryResponseDto> getAllInventoryCategories(Integer userId) {

        List<InventoryCategory> inventoryCategoryList = inventoryCategoryRepository.findAllByUserId(userId);

        return inventoryCategoryList.stream()
                .map(inventoryCategoryMapper::toInventoryCategoryResponseDto)
                .toList();
    }
}
