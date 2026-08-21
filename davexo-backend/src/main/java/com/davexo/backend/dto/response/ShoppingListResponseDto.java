package com.davexo.backend.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Shopping list grouped by business priority")
public class ShoppingListResponseDto {

    @Schema(description = "High priority items: MISSING and TO_REPLACE")
    private List<InventoryItemResponseDto> highPriorityItems;

    @Schema(description = "Medium priority items: LOW_STOCK and NEED_MORE")
    private List<InventoryItemResponseDto> mediumPriorityItems;

    @Schema(description = "Low priority items: OUT_OF_SERVICE")
    private List<InventoryItemResponseDto> lowPriorityItems;

    @Schema(description = "Total number of items included in the shopping list")
    private Integer totalItems;
}