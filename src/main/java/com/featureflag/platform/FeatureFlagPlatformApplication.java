package com.featureflag.platform;

import com.featureflag.platform.common.util.ApiKeyGenerator;
import com.featureflag.platform.domain.entity.*;
import com.featureflag.platform.domain.repository.*;
import com.featureflag.platform.service.FeatureEvaluationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FeatureFlagPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeatureFlagPlatformApplication.class, args);
	}

    /* =========================
       BOOTSTRAP DATA (DEV ONLY)
       ========================= */

	@Bean
	CommandLineRunner setupOrganization(OrganizationRepository orgRepo) {
		return args -> {
			orgRepo.findByName("DemoOrg").orElseGet(() -> {
				Organization org = new Organization();
				org.setName("DemoOrg");
				org.setStatus("ACTIVE");
				orgRepo.save(org);
				System.out.println("✅ Organization inserted");
				return org;
			});
		};
	}

	@Bean
	CommandLineRunner setupProject(
			OrganizationRepository orgRepo,
			ProjectRepository projectRepo
	) {
		return args -> {
			Organization org = orgRepo.findByName("DemoOrg")
					.orElseThrow(() -> new RuntimeException("Organization not found"));

			if (projectRepo.findByOrganizationId(org.getId()).isEmpty()) {
				Project project = new Project();
				project.setName("Web Backend");
				project.setDescription("Main backend service");
				project.setOrganization(org);
				projectRepo.save(project);
				System.out.println("✅ Project inserted");
			}
		};
	}

	@Bean
	CommandLineRunner setupEnvironments(
			ProjectRepository projectRepo,
			EnvironmentRepository envRepo
	) {
		return args -> {
			Project project = projectRepo.findAll().get(0);

			for (String envName : new String[]{"DEV", "STAGING", "PROD"}) {
				envRepo.findByProjectIdAndName(project.getId(), envName)
						.orElseGet(() -> {
							Environment env = new Environment();
							env.setName(envName);
							env.setProject(project);
							envRepo.save(env);
							System.out.println("✅ Environment created: " + envName);
							return env;
						});
			}
		};
	}

	@Bean
	CommandLineRunner setupApiKeys(
			EnvironmentRepository envRepo,
			ApiKeyRepository apiKeyRepo
	) {
		return args -> {
			envRepo.findAll().forEach(env -> {
				boolean hasActiveKey = apiKeyRepo.findAll().stream()
						.anyMatch(k ->
								k.getEnvironment().getId().equals(env.getId())
										&& "ACTIVE".equals(k.getStatus())
						);

				if (!hasActiveKey) {
					ApiKey key = new ApiKey();
					key.setApiKey(ApiKeyGenerator.generate());
					key.setEnvironment(env);
					key.setStatus("ACTIVE");
					apiKeyRepo.save(key);
					System.out.println("🔑 API Key generated for " + env.getName());
				}
			});
		};
	}

	@Bean
	CommandLineRunner setupFeatureAndConfigs(
			ProjectRepository projectRepo,
			FeatureRepository featureRepo,
			FeatureConfigRepository featureConfigRepo,
			EnvironmentRepository envRepo
	) {
		return args -> {
			Project project = projectRepo.findAll().get(0);

			Feature feature = featureRepo
					.findByProjectIdAndFeatureKey(project.getId(), "new_checkout")
					.orElseGet(() -> {
						Feature f = new Feature();
						f.setFeatureKey("new_checkout");
						f.setDescription("New checkout UI");
						f.setProject(project);
						featureRepo.save(f);
						System.out.println("✅ Feature created: new_checkout");
						return f;
					});

			envRepo.findByProjectId(project.getId()).forEach(env -> {
				featureConfigRepo
						.findByFeatureIdAndEnvironmentId(feature.getId(), env.getId())
						.orElseGet(() -> {
							FeatureConfig config = new FeatureConfig();
							config.setFeature(feature);
							config.setEnvironment(env);
							config.setEnabled(env.getName().equals("DEV"));
							config.setRolloutType("BOOLEAN");
							config.setRules(null);
							featureConfigRepo.save(config);
							System.out.println("⚙️ FeatureConfig created for " + env.getName());
							return config;
						});
			});
		};
	}

    /* =========================
       TEMPORARY TEST RUNNER
       ========================= */

	@Bean
	CommandLineRunner testPercentageRollout(
			FeatureEvaluationService service,
			ApiKeyRepository apiKeyRepo
	) {
		return args -> {
			String apiKey = apiKeyRepo.findAll().get(0).getApiKey();

			for (int i = 1; i <= 10; i++) {
				boolean enabled = service.isEnabled(
						apiKey,
						"new_checkout",
						"user-" + i
				);
				System.out.println("user-" + i + " -> " + enabled);
			}
		};
	}
}
