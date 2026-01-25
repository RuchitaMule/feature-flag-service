package com.featureflag.platform.sdk.client;

//package com.featureflag.sdk.client;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCache {

    private static class Entry {
        boolean value;
        long expiresAt;
    }

    private final Map<String, Entry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public InMemoryCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public Boolean get(String key) {
        Entry entry = cache.get(key);
        if (entry == null || Instant.now().toEpochMilli() > entry.expiresAt) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    public void put(String key, boolean value) {
        Entry entry = new Entry();
        entry.value = value;
        entry.expiresAt = Instant.now().toEpochMilli() + ttlMillis;
        cache.put(key, entry);
    }
}
