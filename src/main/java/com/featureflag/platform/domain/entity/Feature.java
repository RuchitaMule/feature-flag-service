package com.featureflag.platform.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "features",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"project_id", "feature_key"})
        }
)
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feature_key", nullable = false)
    private String featureKey; // e.g. new_checkout

    @Column(nullable = true)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public String getFeatureKey() {
        return featureKey;
    }

    public String getDescription() {
        return description;
    }

    public Project getProject() {
        return project;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setFeatureKey(String featureKey) {
        this.featureKey = featureKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
