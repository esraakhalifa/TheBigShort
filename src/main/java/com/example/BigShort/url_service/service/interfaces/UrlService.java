package com.example.BigShort.url_service.service.interfaces;

import com.example.BigShort.url_service.domain.dto.URLRequest;
import com.example.BigShort.url_service.domain.dto.URLResponse;

import java.util.List;

public interface UrlService {

    // Create a new shortened URL (generate shortCode + shortUrl using Base62)
    URLResponse createUrl(URLRequest request);

    // Fetch metadata/info about a URL by its short code
    URLResponse getUrlInfo(String shortCode);


    // Soft delete (set isActive = false)
    void deactivateUrl(Long id);


    // Optional but extremely useful: let the user pick a custom shortcode
    URLResponse createCustomUrl(String customShortCode, URLRequest request);
}
