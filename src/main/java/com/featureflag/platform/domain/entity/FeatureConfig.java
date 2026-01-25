package com.featureflag.platform.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "feature_configs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"feature_id", "environment_id"})
        }
)
public class FeatureConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "rollout_type", nullable = false)
    private String rolloutType; // BOOLEAN, PERCENTAGE, TARGETED

    @Column(columnDefinition = "json")
    private String rules; // JSON rollout rules

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public Feature getFeature() {
        return feature;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getRolloutType() {
        return rolloutType;
    }

    public String getRules() {
        return rules;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setRolloutType(String rolloutType) {
        this.rolloutType = rolloutType;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
