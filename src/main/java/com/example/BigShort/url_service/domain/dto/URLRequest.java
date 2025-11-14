    package com.example.BigShort.url_service.domain.dto;

    import jakarta.validation.constraints.Pattern;
    import lombok.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;
    import java.time.LocalDateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class URLRequest {

        @NotBlank(message = "Original URL is required")
        @Pattern(
                regexp = "^(https?://).+",
                message = "URL must start with http:// or https://"
        )
        private String originalUrl;

        @Size(max = 32, message = "Custom short code must be at most 32 characters")
        @Pattern(
                regexp = "^[0-9a-zA-Z]*$",
                message = "Custom short code can only contain letters and digits"
        )
        private String customShortCode;

        private LocalDateTime expiresAt; // optional, can be null
    }
