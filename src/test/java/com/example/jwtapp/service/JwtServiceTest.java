package com.example.jwtapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.jwtapp.dto.ValidateRequest;
import com.example.jwtapp.dto.ValidateResponse;
import com.example.jwtapp.exception.ResourceNotFoundException;
import com.example.jwtapp.repository.JwtTokenRepository;
import com.example.jwtapp.testutil.TestKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

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
    void validateAndStore_shouldPersistValidToken() {
        ValidateResponse response = jwtService.validateAndStore(new ValidateRequest(TestKeyProvider.createValidToken()));

        assertThat(response.valid()).isTrue();
        assertThat(response.tokenId()).isNotNull();
        assertThat(repository.findById(response.tokenId())).isPresent();
    }

    @Test
    void validateAndStore_shouldPersistInvalidSignatureToken() {
        ValidateResponse response = jwtService.validateAndStore(new ValidateRequest(TestKeyProvider.createInvalidSignatureToken()));

        assertThat(response.valid()).isFalse();
        assertThat(repository.findById(response.tokenId()))
                .isPresent()
                .get()
                .extracting("isValid")
                .isEqualTo(Boolean.FALSE);
    }

    @Test
    void validateAndStore_shouldPersistExpiredToken() {
        ValidateResponse response = jwtService.validateAndStore(new ValidateRequest(TestKeyProvider.createExpiredToken()));

        assertThat(response.valid()).isFalse();
        assertThat(repository.findById(response.tokenId()))
                .isPresent()
                .get()
                .extracting("isValid")
                .isEqualTo(Boolean.FALSE);
    }

    @Test
    void getPayload_shouldReturnStoredPayload() {
        ValidateResponse response = jwtService.validateAndStore(new ValidateRequest(TestKeyProvider.createValidToken()));

        var payload = jwtService.getPayload(response.tokenId());
        assertThat(payload).containsEntry("sub", "user123");
        assertThat(payload).containsEntry("iss", "issuer-demo");
    }

    @Test
    void getPayload_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> jwtService.getPayload(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
