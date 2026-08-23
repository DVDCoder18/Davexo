package com.davexo.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.request.LoginRequestDto;
import com.davexo.backend.dto.request.SignUpRequestDto;
import com.davexo.backend.dto.response.LoginResponseDto;
import com.davexo.backend.exception.CustomErrorResponse;
import com.davexo.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "User authentication and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    @Operation(
            summary = "Log in",
            description = "Authenticates a user and returns a JWT")
    @ApiResponse(
            responseCode = "200",
            description = "Authentication successful",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LoginResponseDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {

        LoginResponseDto response = authService.login(loginRequestDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/sign-up",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    @Operation(
            summary = "Register a user",
            description = "Creates a new user account")
    @ApiResponse(
            responseCode = "201",
            description = "User account created successfully",
            content = @Content)
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Registration rejected by a business rule",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<Void> signUp(
            @Valid @RequestBody SignUpRequestDto signUpRequestDto) {

        authService.signUp(signUpRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
