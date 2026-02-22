package com.kdiachenko.aemupload.config;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.List;

/**
 * Authentication configuration using Adobe service credentials (JWT).
 * This is used for server-to-server authentication with Adobe IMS.
 *
 * <pre>{@code
 * AuthConfig auth = ServiceCredentialsAuthConfig.builder()
 *     .imsEndpoint("https://ims-na1.adobelogin.com/ims/exchange/jwt")
 *     .clientId("your-client-id")
 *     .clientSecret("your-client-secret")
 *     .technicalAccountId("your-technical-account-id")
 *     .orgId("your-org-id@AdobeOrg")
 *     .privateKeyContent("-----BEGIN PRIVATE KEY-----...")
 *     .metaScopes(List.of("ent_aem_cloud_api"))
 *     .build();
 * }</pre>
 *
 * <p>Note: {@code metaScopes} must not be empty. If {@code tokenLifetimeSeconds} is not set,
 * it defaults to {@value #DEFAULT_TOKEN_LIFETIME_SECONDS} seconds.</p>
 */
@Value
@Builder
@ToString(exclude = {"clientSecret", "privateKeyContent"})
public class ServiceCredentialsAuthConfig implements AuthConfig, ApiAccessTokenConfiguration {
    private static final int DEFAULT_TOKEN_LIFETIME_SECONDS = 86400;

    /**
     * Adobe IMS token exchange endpoint (full URL).
     */
    String imsEndpoint;
    String clientId;
    String clientSecret;
    String technicalAccountId;
    String orgId;
    String email;
    /**
     * Adobe I/O meta scopes. Must not be empty.
     */
    List<String> metaScopes;
    /**
     * PEM-encoded private key content in PKCS8 format (BEGIN PRIVATE KEY).
     */
    String privateKeyContent;
    /**
     * File path to a PEM-encoded private key in PKCS8 format.
     */
    String privateKeyFilePath;
    /**
     * JWT token lifetime in seconds. Defaults to {@value #DEFAULT_TOKEN_LIFETIME_SECONDS} if not set.
     */
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
         * @throws IllegalStateException if required fields are missing or invalid
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
            if ((privateKeyContent == null || privateKeyContent.isBlank())
                    && (privateKeyFilePath == null || privateKeyFilePath.isBlank())) {
                throw new IllegalStateException("either privateKeyContent or privateKeyFilePath must be provided");
            }
            if (metaScopes == null || metaScopes.isEmpty()) {
                throw new IllegalStateException("metaScopes must not be null or empty");
            }
            if (tokenLifetimeSeconds <= 0) {
                tokenLifetimeSeconds = DEFAULT_TOKEN_LIFETIME_SECONDS;
            }
            return new ServiceCredentialsAuthConfig(this);
        }
    }
}
