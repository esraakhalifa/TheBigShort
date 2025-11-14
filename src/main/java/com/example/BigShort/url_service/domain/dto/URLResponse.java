package com.example.BigShort.url_service.domain.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class URLResponse {
    private Long id;
    private String shortCode;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private String shortUrl;
}
