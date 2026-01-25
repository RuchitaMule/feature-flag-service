package com.featureflag.platform.api.controller;

import com.featureflag.platform.service.FeatureAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class FeatureAdminController {

    private final FeatureAdminService featureAdminService;

    public FeatureAdminController(FeatureAdminService featureAdminService) {
        this.featureAdminService = featureAdminService;
    }
//
//    @PostMapping("/features/{featureKey}/toggle")
//    public ResponseEntity<Void> toggleFeature(
//            @PathVariable String featureKey,
//            @RequestParam boolean enabled
//    ) {
//        featureAdminService.toggleFeature(featureKey, enabled);
//        return ResponseEntity.ok().build();
//    }


    @PostMapping("/features/{featureKey}/toggle")
    public ResponseEntity<Void> toggleFeature(
            @PathVariable String featureKey,
            @RequestParam boolean enabled,
            @RequestParam String env
    ) {
        featureAdminService.toggleFeature(featureKey, enabled, env);
        return ResponseEntity.ok().build();
    }

}
