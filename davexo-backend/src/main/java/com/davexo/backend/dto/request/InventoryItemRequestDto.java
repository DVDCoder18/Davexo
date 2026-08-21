package com.davexo.backend.dto.request;

import com.davexo.backend.enums.InventoryItemType;
import com.davexo.backend.enums.InventoryStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Request used to create or update an inventory item")
public class InventoryItemRequestDto {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Inventory item name")
    private String name;

    @NotNull
    @Schema(description = "Inventory item type. CONSUMABLE items and DURABLE items use different allowed statuses")
    private InventoryItemType type;

    @NotNull
    @Schema(description = "Inventory item status. CONSUMABLE statuses: IN_STOCK, LOW_STOCK, MISSING. DURABLE statuses: IN_SERVICE, OUT_OF_SERVICE, TO_REPLACE, NEED_MORE")
    private InventoryStatus status;

    @Size(max = 500)
    @Schema(description = "Optional note about the inventory item")
    private String note;

    @NotNull
    @Schema(description = "Identifier of the inventory category associated with the item")
    private Integer inventoryCategoryId;
}