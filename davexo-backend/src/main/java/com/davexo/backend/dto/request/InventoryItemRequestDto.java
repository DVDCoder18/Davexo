package com.davexo.backend.dto.request;

import com.davexo.backend.enums.InventoryItemType;
import com.davexo.backend.enums.InventoryStatus;

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
public class InventoryItemRequestDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private InventoryItemType type;

    @NotNull
    private InventoryStatus status;

    @Size(max = 500)
    private String note;

    @NotNull
    private Integer inventoryCategoryId;
}
