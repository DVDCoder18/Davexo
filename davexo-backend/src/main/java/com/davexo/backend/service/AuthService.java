package com.davexo.backend.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.davexo.backend.dto.request.LoginRequestDto;
import com.davexo.backend.dto.request.SignUpRequestDto;
import com.davexo.backend.dto.response.LoginResponseDto;
import com.davexo.backend.entity.User;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.repository.UserRepository;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${app.allowed-emails}")
    private String allowedEmails;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        String email = loginRequestDto.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginRequestDto.getPassword()));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(customUserDetails);

        return LoginResponseDto.builder()
                .token(token)
                .userId(customUserDetails.getUser().getId())
                .pseudo(customUserDetails.getUser().getPseudo())
                .email(customUserDetails.getUser().getEmail())
                .role(customUserDetails.getUser().getRole())
                .build();
    }
    
    public void signUp(SignUpRequestDto signUpRequestDto) {
        
        List<String> allowedEmailsList = Arrays.stream(allowedEmails.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();

        String email = signUpRequestDto.getEmail().trim().toLowerCase();

        if (!allowedEmailsList.contains(email)) {
            throw new BusinessException("Sign up isn't allowed with this email address.");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Sign up failed. Please check provided information.");
        }

        String passwordHash = passwordEncoder.encode(signUpRequestDto.getPassword());

        User user = User.builder()
                .pseudo(signUpRequestDto.getPseudo().trim())
                .email(email)
                .passwordHash(passwordHash)
                .build();

        userRepository.save(user);
    }
}
