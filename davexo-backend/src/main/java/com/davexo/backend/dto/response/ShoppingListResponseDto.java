package com.davexo.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListResponseDto {

    private List<InventoryItemResponseDto> highPriorityItems;

    private List<InventoryItemResponseDto> mediumPriorityItems;

    private List<InventoryItemResponseDto> lowPriorityItems;

    private Integer totalItems;
}
