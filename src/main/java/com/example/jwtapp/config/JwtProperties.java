package com.example.jwtapp.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank
    private String certificatePath = "classpath:certs/public.crt";

    @Min(1)
    @Max(100000)
    private int maxTokenLength = 4096;

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    public void setMaxTokenLength(int maxTokenLength) {
        this.maxTokenLength = maxTokenLength;
    }
}
