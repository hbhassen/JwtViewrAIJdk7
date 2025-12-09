package com.example.jwtapp.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwtapp.entity.JwtTokenEntity;
import com.example.jwtapp.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

    @Autowired
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody ValidateRequest request) {
        if (request == null || request.getToken() == null || request.getToken().length() == 0) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Le champ token est requis"));
        }
        JwtService.ValidationResult result = jwtService.validateAndStore(request.getToken());
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("tokenId", result.getId());
        response.put("valid", Boolean.valueOf(result.isValid()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payload/{id}")
    public ResponseEntity<?> getPayload(@PathVariable("id") Long id) {
        JwtTokenEntity entity = jwtService.findToken(id);
        if (entity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Token introuvable"));
        }
        try {
            Map payload = objectMapper.readValue(entity.getPayloadJson(), Map.class);
            return ResponseEntity.ok(payload);
        } catch (IOException e) {
            Map<String, Object> fallback = new HashMap<String, Object>();
            fallback.put("rawPayload", entity.getPayloadJson());
            fallback.put("warning", "Impossible d'analyser le JSON, payload brut retourn?.");
            return ResponseEntity.ok(fallback);
        }
    }

    public static class ValidateRequest {
        private String token;

        public ValidateRequest() {
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
