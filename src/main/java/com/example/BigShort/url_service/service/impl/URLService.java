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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class URLService implements UrlService {

    private static final Logger log = LoggerFactory.getLogger(URLService.class);
    private final UrlRepository urlRepository;

    @Value("${app.shortener.domain}")
    private String domain;  // e.g. https://bigshort.com/r/

    // -------------------------------------------------------
    // CREATE SHORT URL (standard)
    // -------------------------------------------------------
    @Override
    @Transactional
    public URLResponse createUrl(URLRequest request) {
        log.info("Creating short URL - urlLength={}, hasExpiry={}",
                request.getOriginalUrl() != null ? request.getOriginalUrl().length() : 0,
                request.getExpiresAt() != null);

        // Validation
        if (request.getOriginalUrl() == null || request.getOriginalUrl().isBlank()) {
            log.warn("URL creation failed - reason=empty_url");
            throw new InvalidUrlException("Original URL cannot be empty");
        }

        if (!request.getOriginalUrl().matches("^(http|https)://.*$")) {
            log.warn("URL creation failed - reason=invalid_format, url={}",
                    sanitizeUrl(request.getOriginalUrl()));
            throw new InvalidUrlException(request.getOriginalUrl());
        }

        try {
            Url url = Url.builder()
                    .originalUrl(request.getOriginalUrl())
                    .createdAt(LocalDateTime.now())
                    .expiresAt(request.getExpiresAt())
                    .isActive(true)
                    .shortCode("TEMP")
                    .shortUrl("TEMP")
                    .build();

            log.debug("Saving URL entity to generate ID");
            url = urlRepository.saveAndFlush(url);

            String shortCode = Base62Encoder.encode(url.getId());
            url.setShortCode(shortCode);
            url.setShortUrl(domain + shortCode);

            log.debug("Updating URL with generated shortCode - id={}, shortCode={}",
                    url.getId(), shortCode);
            url = urlRepository.save(url);

            log.info("Short URL created successfully - shortCode={}, id={}, expiresAt={}",
                    shortCode, url.getId(), url.getExpiresAt());

            return mapToResponse(url);

        } catch (Exception e) {
            log.error("Failed to create short URL - error={}", e.getMessage(), e);
            throw e;
        }
    }

    // -------------------------------------------------------
    // CREATE CUSTOM SHORT URL
    // -------------------------------------------------------
    @Override
    @Transactional
    public URLResponse createCustomUrl(String customShortCode, URLRequest request) {
        log.info("Creating custom short URL - customCode={}, urlLength={}",
                customShortCode,
                request.getOriginalUrl() != null ? request.getOriginalUrl().length() : 0);

        if (urlRepository.existsByShortCode(customShortCode)) {
            log.warn("Custom URL creation failed - reason=code_exists, customCode={}",
                    customShortCode);
            throw new ShortCodeAlreadyExistsException(customShortCode);
        }

        try {
            Url url = Url.builder()
                    .originalUrl(request.getOriginalUrl())
                    .createdAt(LocalDateTime.now())
                    .expiresAt(request.getExpiresAt())
                    .isActive(true)
                    .shortCode(customShortCode)
                    .shortUrl(domain + customShortCode)
                    .build();

            url = urlRepository.save(url);

            log.info("Custom short URL created successfully - shortCode={}, id={}",
                    customShortCode, url.getId());

            return mapToResponse(url);

        } catch (Exception e) {
            log.error("Failed to create custom short URL - customCode={}, error={}",
                    customShortCode, e.getMessage(), e);
            throw e;
        }
    }

    // -------------------------------------------------------
    // GET URL INFO
    // -------------------------------------------------------
    @Override
    public URLResponse getUrlInfo(String shortCode) {
        log.info("Retrieving URL info - shortCode={}", shortCode);

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("URL not found - shortCode={}", shortCode);
                    return new RuntimeException("Short code not found.");
                });

        // Check if expired
        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.info("Expired URL accessed - shortCode={}, expiredAt={}",
                    shortCode, url.getExpiresAt());
        }

        // Check if inactive
        if (!url.getIsActive()) {
            log.info("Inactive URL accessed - shortCode={}", shortCode);
        }

        log.info("URL info retrieved - shortCode={}, isActive={}, hasExpiry={}",
                shortCode, url.getIsActive(), url.getExpiresAt() != null);

        return mapToResponse(url);
    }

    // -------------------------------------------------------
    // DEACTIVATE URL
    // -------------------------------------------------------
    @Override
    @Transactional
    public void deactivateUrl(Long id) {
        log.info("Deactivating URL - id={}", id);

        Url url = urlRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot deactivate - URL not found, id={}", id);
                    return new RuntimeException("URL not found.");
                });

        url.setIsActive(false);
        urlRepository.save(url);

        log.info("URL deactivated successfully - id={}, shortCode={}", id, url.getShortCode());
    }

    // -------------------------------------------------------
    // HELPER: Sanitize URL for logging (remove sensitive params)
    // -------------------------------------------------------
    private String sanitizeUrl(String url) {
        if (url == null) return "null";

        // Remove sensitive query parameters
        return url.replaceAll("([?&])(token|key|secret|password|auth)=[^&]*", "$1$2=***");
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