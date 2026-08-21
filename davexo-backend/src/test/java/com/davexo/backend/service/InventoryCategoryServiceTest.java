package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.davexo.backend.dto.request.InventoryCategoryRequestDto;
import com.davexo.backend.dto.response.InventoryCategoryResponseDto;
import com.davexo.backend.entity.InventoryCategory;
import com.davexo.backend.entity.User;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.InventoryCategoryMapper;
import com.davexo.backend.repository.InventoryCategoryRepository;

@ExtendWith(MockitoExtension.class)
class InventoryCategoryServiceTest {

    private static final Integer USER_ID = 1;
    private static final Integer CATEGORY_ID = 10;

    @Mock
    private InventoryCategoryRepository inventoryCategoryRepository;

    @Mock
    private InventoryCategoryMapper inventoryCategoryMapper;

    private InventoryCategoryService inventoryCategoryService;

    @BeforeEach
    void setUp() {
        inventoryCategoryService = new InventoryCategoryService(
                inventoryCategoryRepository,
                inventoryCategoryMapper);
    }

    @Test
    void addInventoryCategory_shouldRejectDuplicateNameForSameUser() {
        User user = createUser();
        InventoryCategoryRequestDto dto = createRequestDto();

        when(inventoryCategoryRepository.existsByNameIgnoreCaseAndUserId(
                dto.getName(),
                USER_ID))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> inventoryCategoryService.addInventoryCategory(
                        dto,
                        user));

        assertEquals(
                "You already possess an inventory category with this name",
                exception.getMessage());

        verify(inventoryCategoryRepository, never())
                .save(any(InventoryCategory.class));
    }

    @Test
    void addInventoryCategory_shouldAssociateUserAndSaveCategory() {
        User user = createUser();
        InventoryCategoryRequestDto dto = createRequestDto();

        InventoryCategory category = new InventoryCategory();
        InventoryCategory savedCategory = new InventoryCategory();
        InventoryCategoryResponseDto responseDto = new InventoryCategoryResponseDto();

        when(inventoryCategoryRepository.existsByNameIgnoreCaseAndUserId(
                dto.getName(),
                USER_ID))
                .thenReturn(false);

        when(inventoryCategoryMapper.toInventoryCategoryEntity(dto))
                .thenReturn(category);

        when(inventoryCategoryRepository.save(category))
                .thenReturn(savedCategory);

        when(inventoryCategoryMapper.toInventoryCategoryResponseDto(
                savedCategory))
                .thenReturn(responseDto);

        InventoryCategoryResponseDto result = inventoryCategoryService.addInventoryCategory(
                dto,
                user);

        assertSame(
                user,
                category.getUser());

        assertSame(
                responseDto,
                result);
    }

    @Test
    void updateInventoryCategory_shouldRejectDuplicateNameFromAnotherCategory() {
        InventoryCategoryRequestDto dto = createRequestDto();
        InventoryCategory category = new InventoryCategory();

        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(inventoryCategoryRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(
                dto.getName(),
                USER_ID,
                CATEGORY_ID))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> inventoryCategoryService.updateInventoryCategory(
                        dto,
                        CATEGORY_ID,
                        USER_ID));

        assertEquals(
                "You already possess an inventory category with this name",
                exception.getMessage());

        verify(inventoryCategoryRepository, never())
                .save(any(InventoryCategory.class));
    }

    @Test
    void updateInventoryCategory_shouldUpdateNameAndSave() {
        InventoryCategoryRequestDto dto = createRequestDto();
        InventoryCategory category = new InventoryCategory();
        InventoryCategoryResponseDto responseDto = new InventoryCategoryResponseDto();

        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(inventoryCategoryRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(
                dto.getName(),
                USER_ID,
                CATEGORY_ID))
                .thenReturn(false);

        when(inventoryCategoryRepository.save(category))
                .thenReturn(category);

        when(inventoryCategoryMapper.toInventoryCategoryResponseDto(category))
                .thenReturn(responseDto);

        InventoryCategoryResponseDto result = inventoryCategoryService.updateInventoryCategory(
                dto,
                CATEGORY_ID,
                USER_ID);

        assertEquals(
                dto.getName(),
                category.getName());

        assertSame(
                responseDto,
                result);
    }

    @Test
    void deleteInventoryCategory_shouldThrowWhenCategoryIsNotFound() {
        when(inventoryCategoryRepository.deleteByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(0L);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryCategoryService.deleteInventoryCategory(
                        CATEGORY_ID,
                        USER_ID));

        assertEquals(
                "Inventory category not found",
                exception.getMessage());
    }

    @Test
    void getInventoryCategoryDetail_shouldThrowWhenCategoryIsNotFound() {
        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryCategoryService.getInventoryCategoryDetail(
                        CATEGORY_ID,
                        USER_ID));
    }

    @Test
    void getAllInventoryCategories_shouldMapAllCategories() {
        InventoryCategory category1 = new InventoryCategory();
        InventoryCategory category2 = new InventoryCategory();

        InventoryCategoryResponseDto response1 = new InventoryCategoryResponseDto();

        InventoryCategoryResponseDto response2 = new InventoryCategoryResponseDto();

        when(inventoryCategoryRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(
                        category1,
                        category2));

        when(inventoryCategoryMapper.toInventoryCategoryResponseDto(category1))
                .thenReturn(response1);

        when(inventoryCategoryMapper.toInventoryCategoryResponseDto(category2))
                .thenReturn(response2);

        List<InventoryCategoryResponseDto> result = inventoryCategoryService.getAllInventoryCategories(USER_ID);

        assertEquals(
                2,
                result.size());

        assertSame(
                response1,
                result.get(0));

        assertSame(
                response2,
                result.get(1));
    }

    private User createUser() {
        User user = new User();
        user.setId(USER_ID);

        return user;
    }

    private InventoryCategoryRequestDto createRequestDto() {
        InventoryCategoryRequestDto dto = new InventoryCategoryRequestDto();

        dto.setName("Food");

        return dto;
    }
}