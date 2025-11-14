package com.example.BigShort.url_service.repository;

import com.example.BigShort.url_service.domain.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    // Retrieve full entity by short code
    Optional<Url> findByShortCode(String shortCode);

    // Check if a short code already exists
    boolean existsByShortCode(String shortCode);

    // Check if a full short URL already exists
    boolean existsByShortUrl(String shortUrl);

}
