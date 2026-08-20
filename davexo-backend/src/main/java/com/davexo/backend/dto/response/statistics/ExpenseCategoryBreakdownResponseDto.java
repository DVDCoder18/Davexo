package com.davexo.backend.dto.response.statistics;

import java.math.BigDecimal;

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
public class ExpenseCategoryBreakdownResponseDto {

    private Integer categoryId;

    private String categoryName;

    private BigDecimal totalAmount;

    private BigDecimal percentage;

}
