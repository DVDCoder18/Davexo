package com.davexo.backend.dto.response;

import com.davexo.backend.enums.Role;

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
public class LoginResponseDto {

    private String token;

    private Integer userId;
    
    private String pseudo;

    private String email;

    private Role role;
}
