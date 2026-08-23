package com.davexo.backend.dto.response;

import com.davexo.backend.enums.InventoryItemType;
import com.davexo.backend.enums.InventoryStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Inventory item data")
public class InventoryItemResponseDto {

    @NotNull
    @Schema(description = "Inventory item identifier")
    private Integer id;

    @NotNull
    @Schema(description = "Inventory item name")
    private String name;

    @NotNull
    @Schema(description = "Inventory item type")
    private InventoryItemType type;

    @NotNull
    @Schema(description = "Current inventory item status")
    private InventoryStatus status;

    @Schema(description = "Optional note about the inventory item", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String note;

    @NotNull
    @Schema(description = "Inventory category identifier")
    private Integer inventoryCategoryId;

    @NotNull
    @Schema(description = "Inventory category name")
    private String inventoryCategoryName;
}