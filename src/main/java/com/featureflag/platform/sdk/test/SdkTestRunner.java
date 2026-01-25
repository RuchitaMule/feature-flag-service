package com.featureflag.platform.sdk.test;//package com.featureflag.platform.sdk.test;
//
////package com.featureflag.sdk.test;
//

import com.featureflag.platform.sdk.client.FeatureFlagClient;
import com.featureflag.platform.sdk.client.FeatureFlagClientConfig;

////import com.featureflag.sdk.client.FeatureFlagClientConfig;
////import com.featureflag.sdk.http.FeatureFlagHttpClient;
//
//import com.featureflag.platform.sdk.client.FeatureFlagClientConfig;
//import com.featureflag.platform.sdk.http.FeatureFlagHttpClient;
//
//public class SdkTestRunner {
//
//    public static void main(String[] args) {
//
//        FeatureFlagClientConfig config =
//                new FeatureFlagClientConfig(
//                        "http://localhost:8080",
//                        "PUT_YOUR_REAL_API_KEY_HERE",
//                        2000
//                );
//
//        FeatureFlagHttpClient client =
//                new FeatureFlagHttpClient(config);
//
//        boolean enabled = client.evaluate("new_checkout", "user-123");
//
//        System.out.println("Feature new_checkout enabled? " + enabled);
//    }
//}
public class SdkTestRunner {
    public static void main(String[] args) {

        FeatureFlagClientConfig config =
                FeatureFlagClientConfig.builder()
                        .apiKey("YOUR_API_KEY")
                        .baseUrl("http://localhost:8080")
                        .build();

        FeatureFlagClient client =
                FeatureFlagClient.builder(config);

        boolean enabled =
                client.isEnabled("new_checkout", "user-10");

        System.out.println("Feature enabled = " + enabled);
    }
}
