package com.featureflag.platform.api.dto;

public class FeatureEvaluationResponse {

    private boolean enabled;

    public FeatureEvaluationResponse(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
