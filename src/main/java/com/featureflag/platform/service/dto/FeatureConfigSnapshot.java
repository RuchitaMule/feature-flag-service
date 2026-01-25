package com.featureflag.platform.service.dto;

import java.io.Serializable;

public record FeatureConfigSnapshot(
        boolean enabled,
        String rolloutType,
        String rules
) implements Serializable {}
