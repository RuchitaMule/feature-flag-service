//package com.featureflag.platform.domain.repository;
//
//import com.featureflag.platform.domain.entity.Feature;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface FeatureRepository extends JpaRepository<Feature, Long> {
//
//    Optional<Feature> findByProjectIdAndFeatureKey(Long projectId, String featureKey);
//}


package com.featureflag.platform.domain.repository;

import com.featureflag.platform.domain.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long> {

    Optional<Feature> findByProjectIdAndFeatureKey(Long projectId, String featureKey);
}
