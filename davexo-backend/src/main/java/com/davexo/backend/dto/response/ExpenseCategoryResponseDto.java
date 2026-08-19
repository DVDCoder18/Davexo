package com.davexo.backend.dto.response;

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
public class ExpenseCategoryResponseDto {

    private Integer id;

    private String name;

    private Integer budgetId;

    private String budgetName;
}
