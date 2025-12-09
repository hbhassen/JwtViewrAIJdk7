package com.example.jwtapp.service;

import com.example.jwtapp.config.JwtProperties;
import com.example.jwtapp.domain.entity.JwtTokenEntity;
import com.example.jwtapp.dto.ValidateRequest;
import com.example.jwtapp.dto.ValidateResponse;
import com.example.jwtapp.exception.ResourceNotFoundException;
import com.example.jwtapp.jwt.JwtPayloadDecoder;
import com.example.jwtapp.jwt.JwtValidator;
import com.example.jwtapp.jwt.TokenSanitizer;
import com.example.jwtapp.repository.JwtTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final JwtTokenRepository repository;
    private final JwtPayloadDecoder payloadDecoder;
    private final JwtValidator validator;
    private final JwtProperties properties;
    private final TokenSanitizer tokenSanitizer;
    private final ObjectMapper objectMapper;

    public JwtService(JwtTokenRepository repository,
                      JwtPayloadDecoder payloadDecoder,
                      JwtValidator validator,
                      JwtProperties properties,
                      TokenSanitizer tokenSanitizer,
                      ObjectMapper objectMapper) {
        this.repository = repository;
        this.payloadDecoder = payloadDecoder;
        this.validator = validator;
        this.properties = properties;
        this.tokenSanitizer = tokenSanitizer;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ValidateResponse validateAndStore(ValidateRequest request) {
        var token = request.token();
        enforceTokenLength(token);

        String payloadJson = "{}";
        try {
            payloadJson = payloadDecoder.decodePayload(token);
        } catch (Exception e) {
            log.warn("Echec de décodage du payload JWT ({})", tokenSanitizer.sanitize(token));
        }

        JwtValidator.JwtValidationResult validationResult = validator.validate(token);
        JwtTokenEntity entity = new JwtTokenEntity(token, payloadJson, validationResult.valid());
        JwtTokenEntity saved = repository.save(entity);

        return new ValidateResponse(saved.getId(), validationResult.valid());
    }

    @Transactional
    public Map<String, Object> getPayload(Long id) {
        JwtTokenEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token introuvable pour l'id " + id));
        try {
            return objectMapper.readValue(entity.getPayloadJson(), Map.class);
        } catch (JsonProcessingException e) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("rawPayload", entity.getPayloadJson());
            fallback.put("warning", "Impossible d'analyser le JSON, payload brut retourné.");
            return fallback;
        }
    }

    private void enforceTokenLength(String token) {
        if (token != null && token.length() > properties.getMaxTokenLength()) {
            throw new IllegalArgumentException("Le token dépasse la taille maximale autorisée (" + properties.getMaxTokenLength() + ")");
        }
    }
}
