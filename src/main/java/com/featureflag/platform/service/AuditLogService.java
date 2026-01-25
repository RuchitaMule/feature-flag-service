package com.featureflag.platform.service;

//package com.featureflag.platform.service;

import com.featureflag.platform.domain.entity.AuditLog;
import com.featureflag.platform.domain.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(
            String action,
            String actor,
            String entityType,
            Long entityId,
            String oldValue,
            String newValue,
            Long orgId,
            Long projectId,
            Long envId
    ) {

        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setActor(actor);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setOrganizationId(orgId);
        log.setProjectId(projectId);
        log.setEnvironmentId(envId);

        repository.save(log);
    }
}

