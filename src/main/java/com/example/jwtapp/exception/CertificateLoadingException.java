package com.example.jwtapp.exception;

public class CertificateLoadingException extends RuntimeException {
    public CertificateLoadingException(String message) {
        super(message);
    }

    public CertificateLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
