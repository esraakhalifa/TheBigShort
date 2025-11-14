package com.example.BigShort.url_service.controller;

import com.example.BigShort.url_service.domain.dto.URLRequest;
import com.example.BigShort.url_service.domain.dto.URLResponse;
import com.example.BigShort.url_service.service.impl.URLService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlController {

    private final URLService urlService;

    // -------------------------------------------------------
    // Standard shorten
    // -------------------------------------------------------
    @PostMapping("/shorten")
    public ResponseEntity<URLResponse> shortenUrl(@Valid @RequestBody URLRequest request) {
        URLResponse response = urlService.createUrl(request);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------
    // Custom short code
    // -------------------------------------------------------
    @PostMapping("/custom_shorten")
    public ResponseEntity<URLResponse> customShorten(@Valid @RequestParam String customCode,
                                                     @Valid @RequestBody URLRequest request) {

        URLResponse response = urlService.createCustomUrl(customCode, request);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------
    // Optional: Get URL info by short code
    // -------------------------------------------------------
    @GetMapping("/{shortCode}")
    public ResponseEntity<URLResponse> getUrlInfo(@PathVariable String shortCode) {
        URLResponse response = urlService.getUrlInfo(shortCode);
        return ResponseEntity.ok(response);
    }
}
