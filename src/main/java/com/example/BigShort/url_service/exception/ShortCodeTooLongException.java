package com.example.BigShort.url_service.exception;

public class ShortCodeTooLongException extends RuntimeException {
    public ShortCodeTooLongException(String code, int maxLength) {
        super("The custom short code '" + code + "' is too long. Maximum allowed length is " + maxLength + ".");
    }
}
