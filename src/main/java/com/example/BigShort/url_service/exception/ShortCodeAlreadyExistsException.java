package com.example.BigShort.url_service.exception;

public class ShortCodeAlreadyExistsException extends RuntimeException {
    public ShortCodeAlreadyExistsException(String code) {
        super("Short code '" + code + "' already exists.");
    }
}