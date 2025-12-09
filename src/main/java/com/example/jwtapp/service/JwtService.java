package com.example.jwtapp.service;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jwtapp.entity.JwtTokenEntity;
import com.example.jwtapp.repository.JwtTokenRepository;
import com.example.jwtapp.util.CertificateUtils;
import com.example.jwtapp.util.JwtUtils;

@Service
public class JwtService {

    private static final String CERT_PATH = "certs/public.crt";

    @Autowired
    private JwtTokenRepository repository;

    public ValidationResult validateAndStore(String token) {
        boolean valid = false;
        String payloadJson = JwtUtils.decodePayload(token);

        try {
            X509Certificate certificate = CertificateUtils.loadCertificate(CERT_PATH);
            PublicKey publicKey = CertificateUtils.extractPublicKey(certificate);
            valid = JwtUtils.validateToken(token, publicKey);
        } catch (CertificateException e) {
            valid = false;
        } catch (IOException e) {
            valid = false;
        }

        JwtTokenEntity entity = new JwtTokenEntity(token, payloadJson, Boolean.valueOf(valid), new Date());
        JwtTokenEntity saved = repository.save(entity);
        ValidationResult result = new ValidationResult();
        result.setId(saved.getId());
        result.setValid(valid);
        result.setPayloadJson(payloadJson);
        return result;
    }

    public JwtTokenEntity findToken(Long id) {
        return repository.findOne(id);
    }

    public static class ValidationResult {
        private Long id;
        private boolean valid;
        private String payloadJson;

        public ValidationResult() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getPayloadJson() {
            return payloadJson;
        }

        public void setPayloadJson(String payloadJson) {
            this.payloadJson = payloadJson;
        }
    }
}
