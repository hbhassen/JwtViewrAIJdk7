package com.example.jwtapp.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.jwtapp.exception.PayloadDecodingException;
import org.junit.jupiter.api.Test;

class JwtPayloadDecoderTest {

    private final JwtPayloadDecoder decoder = new JwtPayloadDecoder();

    @Test
    void decodePayload_shouldReturnJsonForValidToken() {
        String payload = "eyJzdWIiOiJ1c2VyIiwiaXNzIjoiZGVtbyJ9"; // {"sub":"user","iss":"demo"}
        String token = "header." + payload + ".signature";

        String decoded = decoder.decodePayload(token);

        assertThat(decoded).contains("\"sub\":\"user\"");
        assertThat(decoded).contains("\"iss\":\"demo\"");
    }

    @Test
    void decodePayload_shouldReturnEmptyObjectWhenMalformed() {
        String decoded = decoder.decodePayload("abc");
        assertThat(decoded).isEqualTo("{}");
    }

    @Test
    void decodePayload_shouldThrowWhenBase64Invalid() {
        String token = "header.!@#.sig";
        assertThatThrownBy(() -> decoder.decodePayload(token))
                .isInstanceOf(PayloadDecodingException.class);
    }
}
