package com.featureflag.platform.domain.repository;

import com.featureflag.platform.domain.entity.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnvironmentRepository extends JpaRepository<Environment, Long> {

    Optional<Environment> findByProjectIdAndName(Long projectId, String name);

    List<Environment> findByProjectId(Long projectId);
}
