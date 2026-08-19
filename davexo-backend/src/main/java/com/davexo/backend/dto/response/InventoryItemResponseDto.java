package com.davexo.backend.dto.response;

import com.davexo.backend.enums.InventoryItemType;
import com.davexo.backend.enums.InventoryStatus;

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
public class InventoryItemResponseDto {

    private Integer id;

    private String name;

    private InventoryItemType type;

    private InventoryStatus status;

    private String note;

    private Integer inventoryCategoryId;

    private String inventoryCategoryName;
}
