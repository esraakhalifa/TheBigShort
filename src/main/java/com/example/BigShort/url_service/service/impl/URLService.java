package com.example.BigShort.url_service.service.impl;

import com.example.BigShort.url_service.domain.dto.URLRequest;
import com.example.BigShort.url_service.domain.dto.URLResponse;
import com.example.BigShort.url_service.domain.entity.Url;
import com.example.BigShort.url_service.exception.InvalidUrlException;
import com.example.BigShort.url_service.exception.ShortCodeAlreadyExistsException;
import com.example.BigShort.url_service.repository.UrlRepository;
import com.example.BigShort.url_service.service.interfaces.UrlService;
import com.example.BigShort.url_service.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class URLService implements UrlService {

    private final UrlRepository urlRepository;

    @Value("${app.shortener.domain}")
    private String domain;  // e.g. https://bigshort.com/r/

    // -------------------------------------------------------
    // CREATE SHORT URL (standard)
    // -------------------------------------------------------
    @Override
    @Transactional
    public URLResponse createUrl(URLRequest request) {

        if (request.getOriginalUrl() == null || request.getOriginalUrl().isBlank()) {
            throw new InvalidUrlException("Original URL cannot be empty");
        }


        if (!request.getOriginalUrl().matches("^(http|https)://.*$")) {
            throw new InvalidUrlException(request.getOriginalUrl());
        }

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .expiresAt(request.getExpiresAt())
                .isActive(true)
                .shortCode("TEMP") // Temporary placeholder to satisfy NOT NULL constraint
                .shortUrl("TEMP") // Temporary placeholder
                .build();

        // First save to generate ID
        url = urlRepository.saveAndFlush(url);

        // Generate short code based on ID
        String shortCode = Base62Encoder.encode(url.getId());
        url.setShortCode(shortCode);
        url.setShortUrl(domain + shortCode);

        // Second save to persist actual shortCode and shortUrl
        url = urlRepository.save(url);

        return mapToResponse(url);
    }



    // -------------------------------------------------------
    // CREATE CUSTOM SHORT URL
    // -------------------------------------------------------
    @Override
    @Transactional
    public URLResponse createCustomUrl(String customShortCode, URLRequest request) {

        if (urlRepository.existsByShortCode(customShortCode)) {
            throw new ShortCodeAlreadyExistsException(customShortCode);
        }

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .expiresAt(request.getExpiresAt())
                .isActive(true)
                .shortCode(customShortCode) // Set immediately to avoid NULL
                .shortUrl(domain + customShortCode) // Set immediately
                .build();

        // Single save with all data
        url = urlRepository.save(url);

        return mapToResponse(url);
    }

    // -------------------------------------------------------
    // GET URL INFO
    // -------------------------------------------------------
    @Override
    public URLResponse getUrlInfo(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short code not found."));

        return mapToResponse(url);
    }


    // -------------------------------------------------------
    // DEACTIVATE URL
    // -------------------------------------------------------
    @Override
    @Transactional
    public void deactivateUrl(Long id) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found."));

        url.setIsActive(false);
        urlRepository.save(url);
    }

    // -------------------------------------------------------
    // MAPPER
    // -------------------------------------------------------
    private URLResponse mapToResponse(Url url) {
        return URLResponse.builder()
                .id(url.getId())
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .isActive(url.getIsActive())
                .shortUrl(url.getShortUrl())
                .build();
    }
}