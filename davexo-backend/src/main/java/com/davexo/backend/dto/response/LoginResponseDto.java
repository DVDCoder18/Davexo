package com.davexo.backend.dto.response;

import com.davexo.backend.enums.Role;

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
@Schema(description = "Authentication result returned after a successful login")
public class LoginResponseDto {

    @NotNull
    @Schema(description = "JWT access token")
    private String token;

    @NotNull
    @Schema(description = "Authenticated user identifier")
    private Integer userId;

    @NotNull
    @Schema(description = "Authenticated user's pseudo")
    private String pseudo;

    @NotNull
    @Schema(description = "Authenticated user's email")
    private String email;

    @NotNull
    @Schema(description = "Authenticated user's role")
    private Role role;
}