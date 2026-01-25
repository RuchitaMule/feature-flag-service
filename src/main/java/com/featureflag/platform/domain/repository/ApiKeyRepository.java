package com.featureflag.platform.domain.repository;

import com.featureflag.platform.domain.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByApiKeyAndStatus(String apiKey, String status);
}
