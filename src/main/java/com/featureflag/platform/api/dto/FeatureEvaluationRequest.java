package com.featureflag.platform.api.dto;

import jakarta.validation.constraints.NotBlank;

public class FeatureEvaluationRequest {

    @NotBlank
    private String featureKey;

    private String userId;

    public String getFeatureKey() {
        return featureKey;
    }

    public void setFeatureKey(String featureKey) {
        this.featureKey = featureKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
