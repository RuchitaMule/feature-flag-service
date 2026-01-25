package com.featureflag.platform.sdk.http;

//package com.featureflag.sdk.http;

//import com.featureflag.sdk.client.FeatureFlagClientConfig;
//import com.featureflag.sdk.model.EvaluationRequest;
//import com.featureflag.sdk.model.EvaluationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflag.platform.sdk.client.FeatureFlagClientConfig;
import com.featureflag.platform.sdk.model.EvaluationRequest;
import com.featureflag.platform.sdk.model.EvaluationResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FeatureFlagHttpClient {

    private final FeatureFlagClientConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FeatureFlagHttpClient(FeatureFlagClientConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofMillis(config.getTimeoutMs()))
                .build();
    }

    public boolean evaluate(String featureKey, String userId) {
        try {
            EvaluationRequest requestBody =
                    new EvaluationRequest(featureKey, userId);

            String json = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/api/v1/flags/evaluate"))
                    .header("Content-Type", "application/json")
                    .header("X-API-KEY", config.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return false; // fail-safe
            }

            EvaluationResponse result =
                    objectMapper.readValue(response.body(), EvaluationResponse.class);

            return result.isEnabled();

        } catch (Exception e) {
            return false; // fail-safe
        }
    }
}
