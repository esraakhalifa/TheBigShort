package com.example.BigShort.url_service.domain.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ApiError {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private Instant timestamp = Instant.now();
}

