package com.davexo.backend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
public class BudgetRequestDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull
    private List<Integer> followedCategoryIds;
}
