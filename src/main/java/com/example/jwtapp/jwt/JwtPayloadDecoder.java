package com.example.jwtapp.jwt;

import com.example.jwtapp.exception.PayloadDecodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class JwtPayloadDecoder {

    public String decodePayload(String token) {
        if (token == null) {
            return "{}";
        }
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return "{}";
        }
        var payloadPart = parts[1];
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(pad(payloadPart));
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new PayloadDecodingException("Payload JWT illisible", e);
        }
    }

    private String pad(String value) {
        int padding = value.length() % 4;
        if (padding == 0) {
            return value;
        }
        return value + "=".repeat(4 - padding);
    }
}
