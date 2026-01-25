package com.featureflag.platform.sdk.client;


public class FeatureFlagClientConfig {

    private final String apiKey;
    private final String baseUrl;
    private final int timeoutMs;

    private FeatureFlagClientConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.baseUrl = builder.baseUrl;
        this.timeoutMs = builder.timeoutMs;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String apiKey;
        private String baseUrl;
        private int timeoutMs = 2000;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder timeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public FeatureFlagClientConfig build() {
            if (apiKey == null || baseUrl == null) {
                throw new IllegalStateException("apiKey and baseUrl are required");
            }
            return new FeatureFlagClientConfig(this);
        }
    }
}
