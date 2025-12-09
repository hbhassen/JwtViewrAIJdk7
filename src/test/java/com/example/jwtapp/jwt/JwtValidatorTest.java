package com.example.jwtapp.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jwtapp.config.JwtProperties;
import com.example.jwtapp.testutil.TestKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class JwtValidatorTest {

    private JwtValidator validator;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setCertificatePath("file:" + TestKeyProvider.certificatePath());
        CertificateLoader loader = new CertificateLoader(new DefaultResourceLoader(), properties);
        validator = new JwtValidator(loader, new TokenSanitizer());
    }

    @Test
    void shouldValidateValidToken() {
        var result = validator.validate(TestKeyProvider.createValidToken());
        assertThat(result.valid()).isTrue();
    }

    @Test
    void shouldDetectExpiredToken() {
        var result = validator.validate(TestKeyProvider.createExpiredToken());
        assertThat(result.valid()).isFalse();
    }

    @Test
    void shouldDetectInvalidSignature() {
        var result = validator.validate(TestKeyProvider.createInvalidSignatureToken());
        assertThat(result.valid()).isFalse();
    }
}
