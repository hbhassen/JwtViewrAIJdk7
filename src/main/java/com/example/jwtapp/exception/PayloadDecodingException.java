package com.example.jwtapp.exception;

public class PayloadDecodingException extends RuntimeException {
    public PayloadDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
