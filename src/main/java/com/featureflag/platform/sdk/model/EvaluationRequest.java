package com.featureflag.platform.sdk.model;

//package com.featureflag.sdk.model;

public class EvaluationRequest {
    private String featureKey;
    private String userId;

    public EvaluationRequest(String featureKey, String userId) {
        this.featureKey = featureKey;
        this.userId = userId;
    }

    public String getFeatureKey() {
        return featureKey;
    }

    public String getUserId() {
        return userId;
    }
}
