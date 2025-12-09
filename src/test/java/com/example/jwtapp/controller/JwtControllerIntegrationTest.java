package com.example.jwtapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.jwtapp.repository.JwtTokenRepository;
import com.example.jwtapp.testutil.TestKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenRepository repository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("jwt.certificate-path", () -> "file:" + TestKeyProvider.certificatePath());
    }

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void validate_shouldReturnValidTrueAndStore() throws Exception {
        String token = TestKeyProvider.createValidToken();

        String responseBody = mockMvc.perform(post("/api/jwt/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long tokenId = Long.parseLong(responseBody.replaceAll("[^0-9]", ""));
        assertThat(repository.findById(tokenId)).isPresent();
    }

    @Test
    void validate_shouldReturnValidFalseOnInvalidSignature() throws Exception {
        String token = TestKeyProvider.createInvalidSignatureToken();

        mockMvc.perform(post("/api/jwt/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void validate_shouldReturnValidFalseOnExpiredToken() throws Exception {
        String token = TestKeyProvider.createExpiredToken();

        mockMvc.perform(post("/api/jwt/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void getPayload_shouldReturnPayload() throws Exception {
        String token = TestKeyProvider.createValidToken();
        String response = mockMvc.perform(post("/api/jwt/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long tokenId = Long.parseLong(response.replaceAll("[^0-9]", ""));

        mockMvc.perform(get("/api/jwt/payload/{id}", tokenId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").value("user123"))
                .andExpect(jsonPath("$.iss").value("issuer-demo"));
    }

    @Test
    void getPayload_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/jwt/payload/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
