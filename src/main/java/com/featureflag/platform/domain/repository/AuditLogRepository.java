package com.featureflag.platform.domain.repository;

//package com.featureflag.platform.domain.repository;

import com.featureflag.platform.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<AuditLog> findByEntityTypeAndEntityId(
            String entityType,
            Long entityId
    );
}

