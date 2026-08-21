package com.davexo.backend.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.davexo.backend.entity.User;
import com.davexo.backend.enums.Role;
import com.davexo.backend.integration.AbstractIntegrationTest;
import com.davexo.backend.repository.UserRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Transactional
class AuthenticationApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void login_shouldReturnJwtAndAllowAccessToProtectedEndpoint() throws Exception {
        createUser(
                "api@test.com",
                "ApiUser",
                "Password123!");

        String loginRequest = """
                {
                    "email": "api@test.com",
                    "password": "Password123!"
                }
                """;

        MvcResult loginResult = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("api@test.com"))
                .andReturn();

        String responseBody = loginResult
                .getResponse()
                .getContentAsString();

        JsonNode responseJson = jsonMapper.readTree(responseBody);

        String token = responseJson
                .get("token")
                .asString();

        assertFalse(token.isBlank());

        mockMvc.perform(
                get("/api/tasks")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(
                get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnUnauthorizedWithInvalidPassword() throws Exception {
        createUser(
                "api@test.com",
                "ApiUser",
                "Password123!");

        String loginRequest = """
                {
                    "email": "api@test.com",
                    "password": "WrongPassword"
                }
                """;

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    private User createUser(
            String email,
            String pseudo,
            String plainPassword) {

        User user = new User();

        user.setPseudo(pseudo);
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(plainPassword));
        user.setCreatedAt(
                LocalDate.of(2026, 1, 1));
        user.setRole(Role.USER);
        user.setIsActive(true);

        return userRepository.save(user);
    }
}