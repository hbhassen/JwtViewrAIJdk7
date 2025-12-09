package com.example.jwtapp.util;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import org.apache.commons.codec.binary.Base64;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public final class JwtUtils {

    private JwtUtils() {
    }

    public static boolean validateToken(String token, PublicKey publicKey) {
        if (token == null || token.length() == 0 || publicKey == null) {
            return false;
        }
        try {
            Jws<Claims> parsed = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
            return parsed != null;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            return false;
        } catch (MalformedJwtException e) {
            return false;
        } catch (SignatureException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String decodePayload(String token) {
        if (token == null) {
            return "{}";
        }
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return "{}";
        }
        String payloadPart = parts[1];
        String normalized = normalizeBase64Url(payloadPart);
        byte[] decoded = Base64.decodeBase64(normalized);
        try {
            return new String(decoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new String(decoded);
        }
    }

    private static String normalizeBase64Url(String value) {
        String normalized = value.replace('-', '+').replace('_', '/');
        int padding = normalized.length() % 4;
        if (padding > 0) {
            int padLength = 4 - padding;
            StringBuilder builder = new StringBuilder(normalized);
            for (int i = 0; i < padLength; i++) {
                builder.append('=');
            }
            normalized = builder.toString();
        }
        return normalized;
    }
}
