package com.kdiachenko.aemupload.config;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Authentication configuration using Adobe service credentials (JWT).
 * This is used for server-to-server authentication with Adobe IMS.
 *
 * <pre>{@code
 * AuthConfig auth = ServiceCredentialsAuthConfig.builder()
 *     .imsEndpoint("ims-na1.adobelogin.com")
 *     .clientId("your-client-id")
 *     .clientSecret("your-client-secret")
 *     .technicalAccountId("your-technical-account-id")
 *     .orgId("your-org-id@AdobeOrg")
 *     .privateKeyContent("-----BEGIN RSA PRIVATE KEY-----...")
 *     .metaScopes(List.of("ent_aem_cloud_api"))
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class ServiceCredentialsAuthConfig implements AuthConfig, ApiAccessTokenConfiguration {

    String imsEndpoint;
    String clientId;
    String clientSecret;
    String technicalAccountId;
    String orgId;
    String email;
    List<String> metaScopes;
    String privateKeyContent;
    String privateKeyFilePath;
    int tokenLifetimeSeconds;

    private ServiceCredentialsAuthConfig(ServiceCredentialsAuthConfigBuilder builder) {
        this.imsEndpoint = builder.imsEndpoint;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.technicalAccountId = builder.technicalAccountId;
        this.orgId = builder.orgId;
        this.email = builder.email;
        this.metaScopes = List.copyOf(builder.metaScopes);
        this.privateKeyContent = builder.privateKeyContent;
        this.privateKeyFilePath = builder.privateKeyFilePath;
        this.tokenLifetimeSeconds = builder.tokenLifetimeSeconds;
    }

    @Override
    public String getAuthType() {
        return "ServiceCredentials";
    }

    @Override
    public String getId() {
        return technicalAccountId;
    }

    @Override
    public String getOrg() {
        return orgId;
    }

    @Override
    public int getTokenLifeTimeInSec() {
        return tokenLifetimeSeconds;
    }

    @Override
    public String getLocalDevelopmentAccessToken() {
        return null; // Not applicable for service credentials
    }

    /**
     * Builder for ServiceCredentialsAuthConfig.
     */
    public static final class ServiceCredentialsAuthConfigBuilder {

        /**
         * Builds the ServiceCredentialsAuthConfig.
         *
         * @return a new ServiceCredentialsAuthConfig instance
         * @throws IllegalStateException if required fields are missing
         */
        public ServiceCredentialsAuthConfig build() {
            if (clientId == null || clientId.isBlank()) {
                throw new IllegalStateException("clientId must not be null or blank");
            }
            if (clientSecret == null || clientSecret.isBlank()) {
                throw new IllegalStateException("clientSecret must not be null or blank");
            }
            if (technicalAccountId == null || technicalAccountId.isBlank()) {
                throw new IllegalStateException("technicalAccountId must not be null or blank");
            }
            if (orgId == null || orgId.isBlank()) {
                throw new IllegalStateException("orgId must not be null or blank");
            }
            if ((privateKeyContent == null || privateKeyContent.isBlank()) &&
                    (privateKeyFilePath == null || privateKeyFilePath.isBlank())) {
                throw new IllegalStateException("either privateKeyContent or privateKeyFilePath must be provided");
            }
            return new ServiceCredentialsAuthConfig(this);
        }
    }
}
