package com.example.jwtapp.jwt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Component;

@Component
public class TokenSanitizer {

    private static final int PREFIX_LENGTH = 10;

    public String sanitize(String token) {
        if (token == null) {
            return "<null>";
        }
        var prefix = token.length() <= PREFIX_LENGTH ? token : token.substring(0, PREFIX_LENGTH);
        return prefix + "...#" + hash(token);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hashed) {
                builder.append(String.format("%02x", b));
            }
            return builder.substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            return "hashErr";
        }
    }
}
