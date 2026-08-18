package com.davexo.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
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
@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String label;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String note;

    @NotNull
    @Column(nullable = false, name = "expense_date")
    private LocalDate expenseDate;

    @ManyToOne(fetch = FetchType.LAZY ,optional = false)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY ,optional = false)
    @NotNull
    @JoinColumn(name = "expense_category_id", nullable = false)
    private ExpenseCategory expenseCategory;
    
}
