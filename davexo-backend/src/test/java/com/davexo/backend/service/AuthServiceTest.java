package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.davexo.backend.dto.request.LoginRequestDto;
import com.davexo.backend.dto.request.SignUpRequestDto;
import com.davexo.backend.dto.response.LoginResponseDto;
import com.davexo.backend.entity.User;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.repository.UserRepository;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                authenticationManager,
                jwtService,
                passwordEncoder,
                userRepository);

        ReflectionTestUtils.setField(
                authService,
                "allowedEmails",
                "allowed@test.com, second@test.com");
    }

    @Test
    void signUp_shouldRejectEmailNotAllowed() {
        SignUpRequestDto dto = createSignUpRequestDto();
        dto.setEmail("unauthorized@test.com");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.signUp(dto));

        assertEquals(
                "Sign up isn't allowed with this email address.",
                exception.getMessage());

        verify(userRepository, never()).existsByEmailIgnoreCase(any());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void signUp_shouldRejectAlreadyUsedEmail() {
        SignUpRequestDto dto = createSignUpRequestDto();

        when(userRepository.existsByEmailIgnoreCase("allowed@test.com"))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.signUp(dto));

        assertEquals(
                "Sign up failed. Please check provided information.",
                exception.getMessage());

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUp_shouldNormalizeEmailEncodePasswordAndSaveUser() {
        SignUpRequestDto dto = createSignUpRequestDto();

        dto.setEmail("  ALLOWED@Test.COM  ");
        dto.setPseudo("  DavexoUser  ");
        dto.setPassword("plainPassword");

        when(userRepository.existsByEmailIgnoreCase("allowed@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("plainPassword"))
                .thenReturn("encodedPassword");

        authService.signUp(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                "allowed@test.com",
                savedUser.getEmail());

        assertEquals(
                "DavexoUser",
                savedUser.getPseudo());

        assertEquals(
                "encodedPassword",
                savedUser.getPasswordHash());

        verify(passwordEncoder).encode("plainPassword");
    }

    @Test
    void signUp_shouldAcceptAllowedEmailRegardlessOfConfiguredCaseAndSpaces() {
        ReflectionTestUtils.setField(
                authService,
                "allowedEmails",
                "  ALLOWED@TEST.COM  , second@test.com");

        SignUpRequestDto dto = createSignUpRequestDto();
        dto.setEmail("allowed@test.com");

        when(userRepository.existsByEmailIgnoreCase("allowed@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode(dto.getPassword()))
                .thenReturn("encodedPassword");

        authService.signUp(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_shouldNormalizeEmailBeforeAuthentication() {
        LoginRequestDto dto = new LoginRequestDto();

        dto.setEmail("  ALLOWED@Test.COM ");
        dto.setPassword("password");

        User user = new User();
        user.setId(1);
        user.setPseudo("Davexo");
        user.setEmail("allowed@test.com");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(customUserDetails);

        when(customUserDetails.getUser())
                .thenReturn(user);

        when(jwtService.generateToken(customUserDetails))
                .thenReturn("jwt-token");

        LoginResponseDto result = authService.login(dto);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(
                UsernamePasswordAuthenticationToken.class);

        verify(authenticationManager).authenticate(
                tokenCaptor.capture());

        UsernamePasswordAuthenticationToken authenticationToken = tokenCaptor.getValue();

        assertEquals(
                "allowed@test.com",
                authenticationToken.getPrincipal());

        assertEquals(
                "password",
                authenticationToken.getCredentials());

        assertEquals(
                "jwt-token",
                result.getToken());

        assertEquals(
                1,
                result.getUserId());

        assertEquals(
                "Davexo",
                result.getPseudo());

        assertEquals(
                "allowed@test.com",
                result.getEmail());

        verify(jwtService).generateToken(customUserDetails);
    }

    private SignUpRequestDto createSignUpRequestDto() {
        SignUpRequestDto dto = new SignUpRequestDto();

        dto.setPseudo("DavexoUser");
        dto.setEmail("allowed@test.com");
        dto.setPassword("password");

        return dto;
    }
}