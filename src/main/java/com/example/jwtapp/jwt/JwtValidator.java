package com.example.jwtapp.jwt;

import com.example.jwtapp.exception.CertificateLoadingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecurityException;
import java.security.PublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtValidator.class);

    private final CertificateLoader certificateLoader;
    private final TokenSanitizer tokenSanitizer;

    public JwtValidator(CertificateLoader certificateLoader, TokenSanitizer tokenSanitizer) {
        this.certificateLoader = certificateLoader;
        this.tokenSanitizer = tokenSanitizer;
    }

    public JwtValidationResult validate(String token) {
        try {
            PublicKey publicKey = certificateLoader.loadPublicKey();
            Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
            return new JwtValidationResult(true, null);
        } catch (ExpiredJwtException e) {
            log.debug("JWT expiré ({})", tokenSanitizer.sanitize(token));
            return new JwtValidationResult(false, "expired");
        } catch (SecurityException e) {
            log.debug("Signature JWT invalide ({})", tokenSanitizer.sanitize(token));
            return new JwtValidationResult(false, "invalid-signature");
        } catch (JwtException e) {
            log.debug("JWT invalide ({}) : {}", tokenSanitizer.sanitize(token), e.getMessage());
            return new JwtValidationResult(false, "invalid-token");
        } catch (CertificateLoadingException e) {
            log.error("Impossible de valider le JWT, certificat non disponible");
            return new JwtValidationResult(false, "certificate-error");
        }
    }

    public record JwtValidationResult(boolean valid, String reason) {
    }
}
