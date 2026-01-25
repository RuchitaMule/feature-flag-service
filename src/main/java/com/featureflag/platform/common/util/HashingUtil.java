
package com.featureflag.platform.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class HashingUtil {

    private HashingUtil() {
    }

    public static int bucket(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert first 4 bytes to positive int
            int value = ((hash[0] & 0xFF) << 24)
                    | ((hash[1] & 0xFF) << 16)
                    | ((hash[2] & 0xFF) << 8)
                    | (hash[3] & 0xFF);

            return Math.abs(value % 100); // 0–99 bucket
        } catch (Exception e) {
            return 0; // fail-safe
        }
    }
}
