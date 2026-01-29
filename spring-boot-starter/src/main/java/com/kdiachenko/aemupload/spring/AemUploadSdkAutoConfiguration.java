package com.kdiachenko.aemupload.spring;

import com.kdiachenko.aemupload.AemUploadSdk;
import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.config.ServiceCredentialsAuthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;

/**
 * Spring Boot auto-configuration for the AEM Upload SDK.
 *
 * <p>This auto-configuration creates the SDK and exposes its APIs as Spring beans
 * when the following conditions are met:</p>
 * <ul>
 *   <li>{@link AemUploadSdk} is on the classpath</li>
 *   <li>{@code aem.upload.server-url} property is configured</li>
 *   <li>{@code aem.upload.enabled} is {@code true} (default)</li>
 * </ul>
 *
 * <p>Example configuration in application.yml:</p>
 * <pre>{@code
 * aem:
 *   upload:
 *     server-url: https://author.adobeaemcloud.com
 *     auth-type: basic
 *     username: admin
 *     password: admin
 * }</pre>
 *
 * @see AemUploadSdkProperties
 */
@AutoConfiguration
@ConditionalOnClass(AemUploadSdk.class)
@ConditionalOnProperty(prefix = "aem.upload", name = "server-url")
@EnableConfigurationProperties(AemUploadSdkProperties.class)
public class AemUploadSdkAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AemUploadSdkAutoConfiguration.class);

    private final AemUploadSdkProperties properties;
    private AemUploadSdk sdk;

    public AemUploadSdkAutoConfiguration(AemUploadSdkProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates the AEM Upload SDK bean.
     *
     * @return the configured SDK instance
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aem.upload", name = "enabled", havingValue = "true", matchIfMissing = true)
    public AemUploadSdk aemUploadSdk() {
        log.info("Initializing AEM Upload SDK for server: {}", properties.getServerUrl());

        var builder = AemUploadSdk.builder()
                .serverUrl(properties.getServerUrl());

        switch (properties.getAuthType()) {
            case ACCESS_TOKEN:
                String token = properties.getAccessToken();
                if (token == null || token.isBlank()) {
                    throw new IllegalStateException("Access token is required when auth-type is ACCESS_TOKEN");
                }
                builder.withAccessToken(token);
                log.debug("Using access token authentication");
                break;

            case BASIC:
                builder.withBasicAuth(properties.getUsername(), properties.getPassword());
                log.debug("Using basic authentication with username: {}", properties.getUsername());
                break;

            case SERVICE_CREDENTIALS:
                builder.withServiceCredentials(buildServiceCredentials());
                log.debug("Using service credentials authentication");
                break;

            default:
                throw new IllegalStateException("Unknown auth type: " + properties.getAuthType());
        }

        sdk = builder.build();
        log.info("AEM Upload SDK initialized successfully");
        return sdk;
    }

    private ServiceCredentialsAuthConfig buildServiceCredentials() {
        var creds = properties.getServiceCredentials();
        var builder = ServiceCredentialsAuthConfig.builder()
                .clientId(creds.getClientId())
                .clientSecret(creds.getClientSecret())
                .technicalAccountId(creds.getTechnicalAccountId())
                .orgId(creds.getOrgId())
                .metaScopes(creds.getMetaScopes());

        String keyContent = creds.getPrivateKeyContent();
        String keyPath = creds.getPrivateKeyPath();

        if (keyContent != null && !keyContent.isBlank()) {
            builder.privateKeyContent(keyContent);
        } else if (keyPath != null && !keyPath.isBlank()) {
            builder.privateKeyFilePath(keyPath);
        } else {
            throw new IllegalStateException(
                    "Either private-key-content or private-key-path is required for service credentials");
        }

        String imsEndpoint = creds.getImsEndpoint();
        if (imsEndpoint != null && !imsEndpoint.isBlank()) {
            builder.imsEndpoint(imsEndpoint);
        }

        return builder.build();
    }

    /**
     * Creates the Direct Binary Upload API bean.
     *
     * @param sdk the SDK instance
     * @return the API instance
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aem.upload", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DirectBinaryUploadApi directBinaryUploadApi(AemUploadSdk sdk) {
        return sdk.directBinaryUploadApi();
    }

    /**
     * Creates the Asset Folder API bean.
     *
     * @param sdk the SDK instance
     * @return the API instance
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aem.upload", name = "enabled", havingValue = "true", matchIfMissing = true)
    public AssetFolderApi assetFolderApi(AemUploadSdk sdk) {
        return sdk.assetFolderApi();
    }

    /**
     * Creates the Asset Metadata API bean.
     *
     * @param sdk the SDK instance
     * @return the API instance
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aem.upload", name = "enabled", havingValue = "true", matchIfMissing = true)
    public AssetMetadataApi assetMetadataApi(AemUploadSdk sdk) {
        return sdk.assetMetadataApi();
    }

    /**
     * Cleanup on shutdown.
     */
    @PreDestroy
    public void destroy() {
        if (sdk != null) {
            try {
                sdk.close();
                log.info("AEM Upload SDK closed");
            } catch (Exception e) {
                log.warn("Error closing AEM Upload SDK", e);
            }
        }
    }
}
