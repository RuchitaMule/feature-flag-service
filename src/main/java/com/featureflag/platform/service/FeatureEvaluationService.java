//package com.featureflag.platform.service;
//
//import com.featureflag.platform.api.exception.FeatureNotFoundException;
//import com.featureflag.platform.api.exception.InvalidApiKeyException;
//import com.featureflag.platform.common.util.HashingUtil;
//import com.featureflag.platform.domain.entity.ApiKey;
//import com.featureflag.platform.domain.entity.Feature;
//import com.featureflag.platform.domain.entity.FeatureConfig;
//import com.featureflag.platform.domain.repository.ApiKeyRepository;
//import com.featureflag.platform.domain.repository.FeatureConfigRepository;
//import com.featureflag.platform.domain.repository.FeatureRepository;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class FeatureEvaluationService {
//
//    private final ApiKeyRepository apiKeyRepository;
//    private final FeatureRepository featureRepository;
//    private final FeatureConfigRepository featureConfigRepository;
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public FeatureEvaluationService(
//            ApiKeyRepository apiKeyRepository,
//            FeatureRepository featureRepository,
//            FeatureConfigRepository featureConfigRepository,
//            RedisTemplate<String, Object> redisTemplate
//    ) {
//        this.apiKeyRepository = apiKeyRepository;
//        this.featureRepository = featureRepository;
//        this.featureConfigRepository = featureConfigRepository;
//        this.redisTemplate = redisTemplate;
//    }
//
//    /**
//     * Core feature flag evaluation method
//     */
//    @Transactional(readOnly = true)
//    public boolean isEnabled(String apiKeyValue, String featureKey, String userId) {
//
//        // 1️⃣ Validate API Key
//        ApiKey apiKey = apiKeyRepository
//                .findByApiKeyAndStatus(apiKeyValue, "ACTIVE")
//                .orElseThrow(() ->
//                        new InvalidApiKeyException("Invalid or inactive API key")
//                );
//
//
//
//        var environment = apiKey.getEnvironment();
//        var project = environment.getProject();
//
//        // 2️⃣ Find Feature
//        Feature feature = featureRepository
//                .findByProjectIdAndFeatureKey(project.getId(), featureKey)
//                .orElseThrow(() ->
//                        new FeatureNotFoundException("Feature not found: " + featureKey)
//                );
//
//
//        // 3️⃣ Fetch FeatureConfig (Redis → DB fallback)
//        String cacheKey =
//                "feature_config:" + environment.getId() + ":" + featureKey;
//
//        FeatureConfig config =
//                (FeatureConfig) redisTemplate.opsForValue().get(cacheKey);
//
//        if (config == null) {
//            // Cache miss → DB
//            config = featureConfigRepository
//                    .findByFeatureIdAndEnvironmentId(
//                            feature.getId(),
//                            environment.getId()
//                    )
//                    .orElse(null);
//
//            if (config != null) {
//                redisTemplate.opsForValue().set(cacheKey, config);
//            }
//        }
//
//        if (config == null || !config.getEnabled()) {
//            return false;
//        }
//
//        // 4️⃣ Rollout evaluation
//        switch (config.getRolloutType()) {
//
//            case "BOOLEAN":
//                return true;
//
//            case "PERCENTAGE":
//                if (userId == null || config.getRules() == null) {
//                    return false;
//                }
//
//                int percentage = extractPercentage(config.getRules());
//                int bucket = HashingUtil.bucket(userId + featureKey);
//
//                return bucket < percentage;
//
//            default:
//                return false;
//        }
//    }
//
//    private String cacheKey(Long envId, String featureKey) {
//        return "ff:" + envId + ":" + featureKey;
//    }
//
//
//    /**
//     * Extract percentage value from rules JSON
//     * (simple implementation for now)
//     */
//    private int extractPercentage(String rulesJson) {
//        try {
//            String value = rulesJson.replaceAll("\\D+", "");
//            return Integer.parseInt(value);
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//}




package com.featureflag.platform.service;

import com.featureflag.platform.api.exception.FeatureNotFoundException;
import com.featureflag.platform.api.exception.InvalidApiKeyException;
import com.featureflag.platform.common.util.HashingUtil;
import com.featureflag.platform.domain.entity.ApiKey;
import com.featureflag.platform.domain.entity.Feature;
import com.featureflag.platform.domain.entity.FeatureConfig;
import com.featureflag.platform.domain.repository.ApiKeyRepository;
import com.featureflag.platform.domain.repository.FeatureConfigRepository;
import com.featureflag.platform.domain.repository.FeatureRepository;
import com.featureflag.platform.service.dto.FeatureConfigSnapshot;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class FeatureEvaluationService {

    private final ApiKeyRepository apiKeyRepository;
    private final FeatureRepository featureRepository;
    private final FeatureConfigRepository featureConfigRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public FeatureEvaluationService(
            ApiKeyRepository apiKeyRepository,
            FeatureRepository featureRepository,
            FeatureConfigRepository featureConfigRepository,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.featureRepository = featureRepository;
        this.featureConfigRepository = featureConfigRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Core feature flag evaluation (Redis → DB fallback)
     */
    @Transactional(readOnly = true)
    public boolean isEnabled(String apiKeyValue, String featureKey, String userId) {

        // 1️⃣ Validate API key
        ApiKey apiKey = apiKeyRepository
                .findByApiKeyAndStatus(apiKeyValue, "ACTIVE")
                .orElseThrow(() ->
                        new InvalidApiKeyException("Invalid or inactive API key")
                );

        var environment = apiKey.getEnvironment();
        var project = environment.getProject();

        // 2️⃣ Find feature
        Feature feature = featureRepository
                .findByProjectIdAndFeatureKey(project.getId(), featureKey)
                .orElseThrow(() ->
                        new FeatureNotFoundException("Feature not found: " + featureKey)
                );

        // 3️⃣ Redis lookup
        String key = cacheKey(environment.getId(), featureKey);
        Object cached = redisTemplate.opsForValue().get(key);

        FeatureConfigSnapshot snapshot;

        if (cached instanceof FeatureConfigSnapshot cachedSnapshot) {
            snapshot = cachedSnapshot;
        } else {
            // 4️⃣ DB fallback
            FeatureConfig config = featureConfigRepository
                    .findByFeatureIdAndEnvironmentId(
                            feature.getId(),
                            environment.getId()
                    )
                    .orElse(null);

            if (config == null) {
                return false;
            }

            snapshot = new FeatureConfigSnapshot(
                    config.getEnabled(),
                    config.getRolloutType(),
                    config.getRules()
            );

            // 5️⃣ Store snapshot in Redis
//            redisTemplate.opsForValue().set(key, snapshot);
            redisTemplate.opsForValue()
                    .set(key, snapshot, Duration.ofSeconds(60));

        }

        // 6️⃣ Evaluate snapshot
        return evaluate(snapshot, userId, featureKey);
    }

    /* ===================== HELPERS ===================== */

    private String cacheKey(Long envId, String featureKey) {
        return "ff:" + envId + ":" + featureKey;
    }

    private boolean evaluate(
            FeatureConfigSnapshot snapshot,
            String userId,
            String featureKey
    ) {
        if (!snapshot.enabled()) {
            return false;
        }

        switch (snapshot.rolloutType()) {

            case "BOOLEAN":
                return true;

            case "PERCENTAGE":
                if (userId == null || snapshot.rules() == null) {
                    return false;
                }
                int percentage = extractPercentage(snapshot.rules());
                int bucket = HashingUtil.bucket(userId + featureKey);
                return bucket < percentage;

            default:
                return false;
        }
    }

    /**
     * Simple percentage extraction (for now)
     */
    private int extractPercentage(String rulesJson) {
        try {
            String value = rulesJson.replaceAll("\\D+", "");
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
