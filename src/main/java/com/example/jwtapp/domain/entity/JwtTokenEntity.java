package com.example.jwtapp.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "jwt_tokens")
public class JwtTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "original_token", nullable = false)
    private String originalToken;

    @Lob
    @Column(name = "payload_json", nullable = false)
    private String payloadJson;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public JwtTokenEntity() {
    }

    public JwtTokenEntity(String originalToken, String payloadJson, Boolean isValid) {
        this.originalToken = originalToken;
        this.payloadJson = payloadJson;
        this.isValid = isValid;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalToken() {
        return originalToken;
    }

    public void setOriginalToken(String originalToken) {
        this.originalToken = originalToken;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean valid) {
        isValid = valid;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
