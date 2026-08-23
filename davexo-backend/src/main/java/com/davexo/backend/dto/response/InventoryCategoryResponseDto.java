package com.davexo.backend.dto.response;

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
@Schema(description = "Inventory category data")
public class InventoryCategoryResponseDto {

    @NotNull
    @Schema(description = "Inventory category identifier")
    private Integer id;

    @NotNull
    @Schema(description = "Inventory category name")
    private String name;
}