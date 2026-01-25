package com.featureflag.platform.sdk.client;
//package com.featureflag.sdk.client;

//import com.featureflag.sdk.http.FeatureFlagHttpClient;

import com.featureflag.platform.sdk.http.FeatureFlagHttpClient;

public class FeatureFlagClient {

    private final FeatureFlagHttpClient httpClient;
    private final InMemoryCache cache;

    private FeatureFlagClient(FeatureFlagClientConfig config) {
        this.httpClient = new FeatureFlagHttpClient(config);
        this.cache = new InMemoryCache(5000); // 5 sec cache
    }

    public static FeatureFlagClient builder(FeatureFlagClientConfig config) {
        return new FeatureFlagClient(config);
    }

    public boolean isEnabled(String featureKey, String userId) {

        String cacheKey = featureKey + ":" + userId;
        Boolean cached = cache.get(cacheKey);

        if (cached != null) {
            return cached;
        }

        boolean result = httpClient.evaluate(featureKey, userId);
        cache.put(cacheKey, result);

        return result;
    }
}
