//package com.example.BigShort.url_service.controller;
//
//import com.example.BigShort.url_service.domain.dto.URLRequest;
//import com.example.BigShort.url_service.domain.dto.URLResponse;
//import com.example.BigShort.url_service.service.impl.URLService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class UrlController {
//
//    private final URLService urlService;
//
//    // -------------------------------------------------------
//    // Standard shorten
//    // -------------------------------------------------------
//    @PostMapping("/shorten")
//    public ResponseEntity<URLResponse> shortenUrl(@Valid @RequestBody URLRequest request) {
//        URLResponse response = urlService.createUrl(request);
//        return ResponseEntity.ok(response);
//    }
//
//    // -------------------------------------------------------
//    // Custom short code
//    // -------------------------------------------------------
//    @PostMapping("/custom_shorten")
//    public ResponseEntity<URLResponse> customShorten(@Valid @RequestParam String customCode,
//                                                     @Valid @RequestBody URLRequest request) {
//
//        URLResponse response = urlService.createCustomUrl(customCode, request);
//        return ResponseEntity.ok(response);
//    }
//
//    // -------------------------------------------------------
//    // Optional: Get URL info by short code
//    // -------------------------------------------------------
//    @GetMapping("/{shortCode}")
//    public ResponseEntity<URLResponse> getUrlInfo(@PathVariable String shortCode) {
//        URLResponse response = urlService.getUrlInfo(shortCode);
//        return ResponseEntity.ok(response);
//
//    }
////    @GetMapping("/redirect/{shortCode}")
////    public ResponseEntity<URLResponse> redirect(@PathVariable String shortCode){
////        URLResponse response = urlService.getUrlInfo(shortCode);
////        return ResponseEntity.ok(new URLResponse());
////
////    }
//}

package com.example.BigShort.url_service.controller;

import com.example.BigShort.url_service.domain.dto.URLRequest;
import com.example.BigShort.url_service.domain.dto.URLResponse;
import com.example.BigShort.url_service.service.impl.URLService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlController {

    private static final Logger log = LoggerFactory.getLogger(UrlController.class);
    private final URLService urlService;

    // -------------------------------------------------------
    // Standard shorten
    // -------------------------------------------------------
    @PostMapping("/shorten")
    public ResponseEntity<URLResponse> shortenUrl(@Valid @RequestBody URLRequest request,
                                                  HttpServletRequest httpRequest) {
        log.info("POST /api/shorten - ip={}, urlLength={}",
                getClientIp(httpRequest),
                request.getOriginalUrl() != null ? request.getOriginalUrl().length() : 0);

        try {
            URLResponse response = urlService.createUrl(request);

            log.info("POST /api/shorten - status=200, shortCode={}", response.getShortCode());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("POST /api/shorten - status=500, error={}", e.getMessage());
            throw e;
        }
    }

    // -------------------------------------------------------
    // Custom short code
    // -------------------------------------------------------
    @PostMapping("/custom_shorten")
    public ResponseEntity<URLResponse> customShorten(@Valid @RequestParam String customCode,
                                                     @Valid @RequestBody URLRequest request,
                                                     HttpServletRequest httpRequest) {
        log.info("POST /api/custom_shorten - ip={}, customCode={}, urlLength={}",
                getClientIp(httpRequest),
                customCode,
                request.getOriginalUrl() != null ? request.getOriginalUrl().length() : 0);

        try {
            URLResponse response = urlService.createCustomUrl(customCode, request);

            log.info("POST /api/custom_shorten - status=200, shortCode={}", response.getShortCode());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("POST /api/custom_shorten - status=500, customCode={}, error={}",
                    customCode, e.getMessage());
            throw e;
        }
    }

    // -------------------------------------------------------
    // Get URL info by short code
    // -------------------------------------------------------
    @GetMapping("/{shortCode}")
    public ResponseEntity<URLResponse> getUrlInfo(@PathVariable String shortCode,
                                                  HttpServletRequest httpRequest) {
        log.info("GET /api/{} - ip={}, userAgent={}",
                shortCode,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));

        try {
            URLResponse response = urlService.getUrlInfo(shortCode);

            log.info("GET /api/{} - status=200, isActive={}", shortCode, response.getIsActive());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.warn("GET /api/{} - status=404, error={}", shortCode, e.getMessage());
            throw e;
        }
    }

    // -------------------------------------------------------
    // Helper method to extract client IP
    // -------------------------------------------------------
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
