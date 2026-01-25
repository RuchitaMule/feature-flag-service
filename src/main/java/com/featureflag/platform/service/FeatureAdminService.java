//package com.featureflag.platform.service;
////package com.featureflag.platform.service;
//
//import com.featureflag.platform.domain.entity.Feature;
//import com.featureflag.platform.domain.entity.FeatureConfig;
//import com.featureflag.platform.domain.repository.FeatureConfigRepository;
//import com.featureflag.platform.domain.repository.FeatureRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class FeatureAdminService {
//
//    private final FeatureRepository featureRepository;
//    private final FeatureConfigRepository featureConfigRepository;
//    private final AuditLogService auditLogService;
//
//    public FeatureAdminService(
//            FeatureRepository featureRepository,
//            FeatureConfigRepository featureConfigRepository,
//            AuditLogService auditLogService
//    ) {
//        this.featureRepository = featureRepository;
//        this.featureConfigRepository = featureConfigRepository;
//        this.auditLogService = auditLogService;
//    }
//
//    @Transactional
//    public void toggleFeature(String featureKey, boolean enabled, String envName) {
//
//        Feature feature = featureRepository
//                .findAll()
//                .stream()
//                .filter(f -> f.getFeatureKey().equals(featureKey))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Feature not found"));
//
//        FeatureConfig config = featureConfigRepository
//                .findByFeatureIdAndEnvironmentName(feature.getId(), envName)
//                .orElseThrow(() ->
//                        new RuntimeException("Feature config not found for env " + envName));
//
//        boolean oldValue = config.getEnabled();
//        config.setEnabled(enabled);
//        featureConfigRepository.save(config);
//
//        // 🔥 Audit log (this part is correct)
//        auditLogService.log(
//                "FEATURE_TOGGLED",
//                "ADMIN",
//                "FEATURE_CONFIG",
//                config.getId(),
//                "enabled=" + oldValue,
//                "enabled=" + enabled,
//                feature.getProject().getOrganization().getId(),
//                feature.getProject().getId(),
//                config.getEnvironment().getId()
//        );
//    }
//
//
//}



package com.featureflag.platform.service;

import com.featureflag.platform.api.exception.FeatureNotFoundException;
import com.featureflag.platform.domain.entity.Environment;
import com.featureflag.platform.domain.entity.Feature;
import com.featureflag.platform.domain.entity.FeatureConfig;
import com.featureflag.platform.domain.repository.EnvironmentRepository;
import com.featureflag.platform.domain.repository.FeatureConfigRepository;
import com.featureflag.platform.domain.repository.FeatureRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeatureAdminService {

    private final FeatureRepository featureRepository;
    private final FeatureConfigRepository featureConfigRepository;
    private final EnvironmentRepository environmentRepository;
    private final AuditLogService auditLogService;
    private final RedisTemplate<String, Object> redisTemplate;

    public FeatureAdminService(
            FeatureRepository featureRepository,
            FeatureConfigRepository featureConfigRepository,
            EnvironmentRepository environmentRepository,
            AuditLogService auditLogService,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.featureRepository = featureRepository;
        this.featureConfigRepository = featureConfigRepository;
        this.environmentRepository = environmentRepository;
        this.auditLogService = auditLogService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void toggleFeature(String featureKey, boolean enabled, String envName) {

        // 1️⃣ Fetch feature
        Feature feature = featureRepository
                .findAll()
                .stream()
                .filter(f -> f.getFeatureKey().equals(featureKey))
                .findFirst()
                .orElseThrow(() ->
                        new FeatureNotFoundException("Feature not found: " + featureKey)
                );

        // 2️⃣ Fetch environment
        Environment environment = environmentRepository
                .findByProjectIdAndName(
                        feature.getProject().getId(),
                        envName
                )
                .orElseThrow(() ->
                        new RuntimeException("Environment not found: " + envName)
                );

        // 3️⃣ Fetch feature config
        FeatureConfig config = featureConfigRepository
                .findByFeatureIdAndEnvironmentId(
                        feature.getId(),
                        environment.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException("Feature config not found")
                );

        boolean oldValue = config.getEnabled();

        // 4️⃣ Update DB
        config.setEnabled(enabled);
        featureConfigRepository.save(config);

        // 5️⃣ 🔥 Invalidate Redis cache
        String redisKey = cacheKey(environment.getId(), featureKey);
        redisTemplate.delete(redisKey);

        // 6️⃣ Audit log
        auditLogService.log(
                "FEATURE_TOGGLED",
                "ADMIN",
                "FEATURE_CONFIG",
                config.getId(),
                "enabled=" + oldValue,
                "enabled=" + enabled,
                feature.getProject().getOrganization().getId(),
                feature.getProject().getId(),
                environment.getId()
        );
    }

    /* ================== HELPERS ================== */

    private String cacheKey(Long envId, String featureKey) {
        return "ff:" + envId + ":" + featureKey;
    }
}
