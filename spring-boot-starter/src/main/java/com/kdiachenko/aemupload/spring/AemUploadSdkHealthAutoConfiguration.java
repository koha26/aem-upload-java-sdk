package com.kdiachenko.aemupload.spring;

import com.kdiachenko.aemupload.AemUploadSdk;
import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot Actuator health indicator auto-configuration for the AEM Upload SDK.
 *
 * <p>This auto-configuration creates a health indicator that checks connectivity
 * to the AEM server. The health check is performed by calling the Asset Folder API.</p>
 *
 * <p>The health endpoint will show:</p>
 * <ul>
 *   <li><strong>UP</strong> - when the SDK can connect to AEM and retrieve DAM root folder</li>
 *   <li><strong>DOWN</strong> - when the connection fails or returns an error</li>
 * </ul>
 *
 * <p>To disable the health indicator:</p>
 * <pre>{@code
 * management:
 *   health:
 *     aem-upload:
 *       enabled: false
 * }</pre>
 */
@AutoConfiguration
@ConditionalOnClass({HealthIndicator.class, AemUploadSdk.class})
@ConditionalOnBean(AemUploadSdk.class)
@ConditionalOnProperty(prefix = "management.health.aem-upload", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AemUploadSdkHealthAutoConfiguration {

    /**
     * Creates the AEM Upload SDK health indicator.
     *
     * @param assetFolderApi the asset folder API
     * @param properties the SDK properties
     * @return the health indicator
     */
    @Bean
    @ConditionalOnBean(AssetFolderApi.class)
    public HealthIndicator aemUploadSdkHealthIndicator(AssetFolderApi assetFolderApi,
                                                        AemUploadSdkProperties properties) {
        return new AemUploadSdkHealthIndicator(assetFolderApi, properties.getServerUrl());
    }

    /**
     * Health indicator implementation for the AEM Upload SDK.
     */
    public static class AemUploadSdkHealthIndicator implements HealthIndicator {

        private final AssetFolderApi assetFolderApi;
        private final String serverUrl;

        public AemUploadSdkHealthIndicator(AssetFolderApi assetFolderApi, String serverUrl) {
            this.assetFolderApi = assetFolderApi;
            this.serverUrl = serverUrl;
        }

        @Override
        public Health health() {
            try {
                // Try to get the DAM root folder as a health check
                AssetApiResponse<?> response = assetFolderApi.getFolder("/content/dam");

                if (response.isSuccess()) {
                    return Health.up()
                            .withDetail("server", serverUrl)
                            .withDetail("status", "Connected")
                            .build();
                } else {
                    String errorMessage = response.getError()
                            .map(e -> e.getMessage())
                            .orElse("Unknown error");

                    int statusCode = response.getError()
                            .map(e -> e.getHttpStatus())
                            .orElse(0);

                    var healthBuilder = Health.down()
                            .withDetail("server", serverUrl)
                            .withDetail("error", errorMessage);

                    if (statusCode > 0) {
                        healthBuilder.withDetail("statusCode", statusCode);
                    }

                    return healthBuilder.build();
                }
            } catch (Exception e) {
                return Health.down()
                        .withDetail("server", serverUrl)
                        .withException(e)
                        .build();
            }
        }
    }
}
