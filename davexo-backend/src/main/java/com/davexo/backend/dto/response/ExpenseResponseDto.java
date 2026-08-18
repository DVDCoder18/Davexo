package com.davexo.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponseDto {

    private Integer id;

    private String label;

    private BigDecimal amount;

    private String note;
    
    private LocalDate expenseDate;

    private Integer expenseCategoryId;

    private String expenseCategoryName;
}
