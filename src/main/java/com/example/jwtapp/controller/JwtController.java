package com.example.jwtapp.controller;

import com.example.jwtapp.dto.ValidateRequest;
import com.example.jwtapp.dto.ValidateResponse;
import com.example.jwtapp.jwt.TokenSanitizer;
import com.example.jwtapp.service.JwtService;
import jakarta.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jwt")
@Validated
public class JwtController {

    private static final Logger log = LoggerFactory.getLogger(JwtController.class);

    private final JwtService jwtService;
    private final TokenSanitizer tokenSanitizer;

    public JwtController(JwtService jwtService, TokenSanitizer tokenSanitizer) {
        this.jwtService = jwtService;
        this.tokenSanitizer = tokenSanitizer;
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validateToken(@Valid @RequestBody ValidateRequest request) {
        log.info("Requête de validation reçue pour JWT {}", tokenSanitizer.sanitize(request.token()));
        ValidateResponse response = jwtService.validateAndStore(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payload/{id}")
    public ResponseEntity<Map<String, Object>> getPayload(@PathVariable("id") Long id) {
        Map<String, Object> payload = jwtService.getPayload(id);
        return ResponseEntity.ok(payload);
    }
}
