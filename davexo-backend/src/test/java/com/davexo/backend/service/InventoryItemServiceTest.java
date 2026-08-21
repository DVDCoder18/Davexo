package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class InventoryItemServiceTest {

    private static final Integer USER_ID = 1;
    private static final Integer ITEM_ID = 10;
    private static final Integer CATEGORY_ID = 20;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private InventoryCategoryRepository inventoryCategoryRepository;

    @Mock
    private InventoryItemMapper inventoryItemMapper;

    private InventoryItemService inventoryItemService;

    @BeforeEach
    void setUp() {
        inventoryItemService = new InventoryItemService(
                inventoryItemRepository,
                inventoryCategoryRepository,
                inventoryItemMapper);
    }

    @Test
    void addInventoryItem_shouldAcceptConsumableWithConsumableStatus() {
        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.CONSUMABLE,
                InventoryStatus.LOW_STOCK);

        User user = createUser();
        InventoryItem item = new InventoryItem();
        InventoryCategory category = new InventoryCategory();

        when(inventoryItemMapper.toInventoryItemEntity(dto))
                .thenReturn(item);

        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(inventoryItemRepository.save(item))
                .thenReturn(item);

        when(inventoryItemMapper.toInventoryItemResponseDto(item))
                .thenReturn(new InventoryItemResponseDto());

        inventoryItemService.addInventoryItem(
                dto,
                user);

        assertSame(
                user,
                item.getUser());

        assertSame(
                category,
                item.getInventoryCategory());

        verify(inventoryItemRepository).save(item);
    }

    @Test
    void addInventoryItem_shouldAcceptDurableWithDurableStatus() {
        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.DURABLE,
                InventoryStatus.TO_REPLACE);

        User user = createUser();
        InventoryItem item = new InventoryItem();
        InventoryCategory category = new InventoryCategory();

        when(inventoryItemMapper.toInventoryItemEntity(dto))
                .thenReturn(item);

        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(inventoryItemRepository.save(item))
                .thenReturn(item);

        when(inventoryItemMapper.toInventoryItemResponseDto(item))
                .thenReturn(new InventoryItemResponseDto());

        inventoryItemService.addInventoryItem(
                dto,
                user);

        verify(inventoryItemRepository).save(item);
    }

    @Test
    void addInventoryItem_shouldRejectConsumableWithDurableStatus() {
        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.CONSUMABLE,
                InventoryStatus.OUT_OF_SERVICE);

        User user = createUser();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> inventoryItemService.addInventoryItem(
                        dto,
                        user));

        assertEquals(
                "The inventory item type and inventory status combination is invalid",
                exception.getMessage());

        verify(inventoryCategoryRepository, never())
                .findByIdAndUserId(any(), any());

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void addInventoryItem_shouldRejectDurableWithConsumableStatus() {
        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.DURABLE,
                InventoryStatus.MISSING);

        User user = createUser();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> inventoryItemService.addInventoryItem(
                        dto,
                        user));

        assertEquals(
                "The inventory item type and inventory status combination is invalid",
                exception.getMessage());

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void addInventoryItem_shouldRejectCategoryNotOwnedByUser() {
        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.CONSUMABLE,
                InventoryStatus.IN_STOCK);

        User user = createUser();

        when(inventoryItemMapper.toInventoryItemEntity(dto))
                .thenReturn(new InventoryItem());

        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryItemService.addInventoryItem(
                        dto,
                        user));

        assertEquals(
                "Inventory category not found",
                exception.getMessage());

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void updateInventoryItem_shouldRejectInvalidTypeStatusCombinationBeforeSaving() {
        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.DURABLE,
                InventoryStatus.MISSING);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> inventoryItemService.updateInventoryItem(
                        dto,
                        ITEM_ID,
                        USER_ID));

        assertEquals(
                "The inventory item type and inventory status combination is invalid",
                exception.getMessage());

        verify(inventoryItemRepository, never())
                .findByIdAndUserId(any(), any());

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void updateInventoryItem_shouldRejectInventoryItemNotOwnedByUser() {
        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.CONSUMABLE,
                InventoryStatus.IN_STOCK);

        when(inventoryItemRepository.findByIdAndUserId(
                ITEM_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryItemService.updateInventoryItem(
                        dto,
                        ITEM_ID,
                        USER_ID));

        assertEquals(
                "Inventory item not found",
                exception.getMessage());

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void updateInventoryItem_shouldRejectCategoryNotOwnedByUser() {
        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.CONSUMABLE,
                InventoryStatus.LOW_STOCK);

        InventoryItem item = new InventoryItem();

        when(inventoryItemRepository.findByIdAndUserId(
                ITEM_ID,
                USER_ID))
                .thenReturn(Optional.of(item));

        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryItemService.updateInventoryItem(
                        dto,
                        ITEM_ID,
                        USER_ID));

        assertEquals(
                "Inventory item category not found",
                exception.getMessage());

        verify(inventoryItemRepository, never())
                .save(any(InventoryItem.class));
    }

    @Test
    void updateInventoryItem_shouldAllowChangingTypeWhenNewStatusIsCompatible() {
        InventoryItem item = new InventoryItem();

        item.setType(InventoryItemType.CONSUMABLE);
        item.setStatus(InventoryStatus.IN_STOCK);

        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.DURABLE,
                InventoryStatus.IN_SERVICE);

        InventoryCategory category = new InventoryCategory();

        when(inventoryItemRepository.findByIdAndUserId(
                ITEM_ID,
                USER_ID))
                .thenReturn(Optional.of(item));

        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(inventoryItemRepository.save(item))
                .thenReturn(item);

        when(inventoryItemMapper.toInventoryItemResponseDto(item))
                .thenReturn(new InventoryItemResponseDto());

        inventoryItemService.updateInventoryItem(
                dto,
                ITEM_ID,
                USER_ID);

        assertEquals(
                InventoryItemType.DURABLE,
                item.getType());

        assertEquals(
                InventoryStatus.IN_SERVICE,
                item.getStatus());

        assertSame(
                category,
                item.getInventoryCategory());
    }

    @Test
    void updateInventoryItem_shouldUpdateFieldsWhenCombinationIsValid() {
        InventoryItem item = new InventoryItem();

        InventoryItemRequestDto dto = createRequestDto(
                InventoryItemType.CONSUMABLE,
                InventoryStatus.MISSING);

        dto.setName("Milk");
        dto.setNote("Buy two");

        InventoryCategory category = new InventoryCategory();
        InventoryItemResponseDto responseDto = new InventoryItemResponseDto();

        when(inventoryItemRepository.findByIdAndUserId(
                ITEM_ID,
                USER_ID))
                .thenReturn(Optional.of(item));

        when(inventoryCategoryRepository.findByIdAndUserId(
                CATEGORY_ID,
                USER_ID))
                .thenReturn(Optional.of(category));

        when(inventoryItemRepository.save(item))
                .thenReturn(item);

        when(inventoryItemMapper.toInventoryItemResponseDto(item))
                .thenReturn(responseDto);

        InventoryItemResponseDto result = inventoryItemService.updateInventoryItem(
                dto,
                ITEM_ID,
                USER_ID);

        assertEquals(
                "Milk",
                item.getName());

        assertEquals(
                InventoryItemType.CONSUMABLE,
                item.getType());

        assertEquals(
                InventoryStatus.MISSING,
                item.getStatus());

        assertEquals(
                "Buy two",
                item.getNote());

        assertSame(
                category,
                item.getInventoryCategory());

        assertSame(
                responseDto,
                result);
    }

    @Test
    void deleteInventoryItem_shouldThrowWhenItemIsNotFound() {
        when(inventoryItemRepository.deleteByIdAndUserId(
                ITEM_ID,
                USER_ID))
                .thenReturn(0L);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryItemService.deleteInventoryItem(
                        ITEM_ID,
                        USER_ID));

        assertEquals(
                "Inventory item not found",
                exception.getMessage());
    }

    private User createUser() {
        User user = new User();
        user.setId(USER_ID);

        return user;
    }

    private InventoryItemRequestDto createRequestDto(
            InventoryItemType type,
            InventoryStatus status) {

        InventoryItemRequestDto dto = new InventoryItemRequestDto();

        dto.setName("Item");
        dto.setType(type);
        dto.setStatus(status);
        dto.setNote("Note");
        dto.setInventoryCategoryId(CATEGORY_ID);

        return dto;
    }
}