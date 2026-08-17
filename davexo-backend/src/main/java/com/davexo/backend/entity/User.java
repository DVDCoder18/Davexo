package com.davexo.backend.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import com.davexo.backend.enums.Role;

import jakarta.persistence.*;
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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String pseudo;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @NotBlank
    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @NotNull
    @Column(nullable = false, name = "created_at")
    private LocalDate createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    @NotNull
    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
    }
}
