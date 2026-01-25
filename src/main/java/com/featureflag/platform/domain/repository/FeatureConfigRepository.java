//package com.featureflag.platform.domain.repository;
//
//import com.featureflag.platform.domain.entity.FeatureConfig;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface FeatureConfigRepository extends JpaRepository<FeatureConfig, Long> {
//
//    Optional<FeatureConfig> findByFeatureIdAndEnvironmentId(
//            Long featureId,
//            Long environmentId
//    );
//}
//


package com.featureflag.platform.domain.repository;

import com.featureflag.platform.domain.entity.FeatureConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FeatureConfigRepository extends JpaRepository<FeatureConfig, Long> {

    Optional<FeatureConfig> findByFeatureIdAndEnvironmentId(
            Long featureId,
            Long environmentId
    );

    // ✅ ADD THIS (for admin toggle use-case)
    Optional<FeatureConfig> findByFeatureId(Long featureId);

    @Query("""
        SELECT fc FROM FeatureConfig fc
        WHERE fc.feature.id = :featureId
          AND fc.environment.name = :envName
    """)
    Optional<FeatureConfig> findByFeatureIdAndEnvironmentName(
            @Param("featureId") Long featureId,
            @Param("envName") String envName
    );

}

