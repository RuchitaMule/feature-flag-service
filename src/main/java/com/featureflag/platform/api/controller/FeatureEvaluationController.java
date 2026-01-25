package com.featureflag.platform.api.controller;

import com.featureflag.platform.api.dto.FeatureEvaluationRequest;
import com.featureflag.platform.api.dto.FeatureEvaluationResponse;
import com.featureflag.platform.service.FeatureEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flags")
public class FeatureEvaluationController {

    private final FeatureEvaluationService featureEvaluationService;

    public FeatureEvaluationController(
            FeatureEvaluationService featureEvaluationService
    ) {
        this.featureEvaluationService = featureEvaluationService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<FeatureEvaluationResponse> evaluate(
            @RequestHeader("X-API-KEY") String apiKey,
            @Valid @RequestBody FeatureEvaluationRequest request
    ) {

        boolean enabled = featureEvaluationService.isEnabled(
                apiKey,
                request.getFeatureKey(),
                request.getUserId()
        );

        return ResponseEntity.ok(
                new FeatureEvaluationResponse(enabled)
        );
    }
}
