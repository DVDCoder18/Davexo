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
@Schema(description = "Expense category data")
public class ExpenseCategoryResponseDto {

    @NotNull
    @Schema(description = "Expense category identifier")
    private Integer id;

    @NotNull
    @Schema(description = "Expense category name")
    private String name;

    @Schema(description = "Associated budget identifier, or null if no budget is associated", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private Integer budgetId;

    @Schema(description = "Associated budget name, or null if no budget is associated", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String budgetName;
}