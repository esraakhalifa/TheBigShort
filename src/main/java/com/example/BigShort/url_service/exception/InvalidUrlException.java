package com.example.BigShort.url_service.exception;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(String url) {
        super("The provided URL '" + url + "' is empty or invalid.");
    }
}