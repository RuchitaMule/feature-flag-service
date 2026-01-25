package com.featureflag.platform.common.util;

import java.security.SecureRandom;
import java.util.HexFormat;

public final class ApiKeyGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    private ApiKeyGenerator() {
    }

    public static String generate() {
        byte[] randomBytes = new byte[32]; // 256-bit
        secureRandom.nextBytes(randomBytes);
        return "ffp_" + HexFormat.of().formatHex(randomBytes);
    }
}
