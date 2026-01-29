package com.kdiachenko.aemupload.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for the AEM Upload SDK.
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
 */
@ConfigurationProperties(prefix = "aem.upload")
public class AemUploadSdkProperties {

    /**
     * Whether the SDK auto-configuration is enabled.
     */
    private boolean enabled = true;

    /**
     * The AEM server URL (e.g., https://author.adobeaemcloud.com or http://localhost:4502).
     */
    private String serverUrl;

    /**
     * The authentication type: ACCESS_TOKEN, BASIC, or SERVICE_CREDENTIALS.
     */
    private AuthType authType = AuthType.BASIC;

    /**
     * Static access token (for development). Used when authType = ACCESS_TOKEN.
     */
    private String accessToken;

    /**
     * Username for basic authentication. Used when authType = BASIC.
     */
    private String username = "admin";

    /**
     * Password for basic authentication. Used when authType = BASIC.
     */
    private String password = "admin";

    /**
     * Service credentials configuration. Used when authType = SERVICE_CREDENTIALS.
     */
    private ServiceCredentials serviceCredentials = new ServiceCredentials();

    public enum AuthType {
        ACCESS_TOKEN,
        BASIC,
        SERVICE_CREDENTIALS
    }

    /**
     * Service credentials for JWT authentication with Adobe I/O.
     */
    public static class ServiceCredentials {
        /**
         * Adobe I/O client ID.
         */
        private String clientId;

        /**
         * Adobe I/O client secret.
         */
        private String clientSecret;

        /**
         * Adobe I/O technical account ID.
         */
        private String technicalAccountId;

        /**
         * Adobe organization ID (e.g., XXXXX@AdobeOrg).
         */
        private String orgId;

        /**
         * Path to the private key file.
         */
        private String privateKeyPath;

        /**
         * PEM-encoded private key content (alternative to privateKeyPath).
         */
        private String privateKeyContent;

        /**
         * Adobe I/O meta scopes.
         */
        private List<String> metaScopes = new ArrayList<>(List.of("ent_aem_cloud_api"));

        /**
         * Adobe IMS endpoint URL.
         */
        private String imsEndpoint = "https://ims-na1.adobelogin.com/ims/exchange/jwt";

        // Getters and setters

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getTechnicalAccountId() {
            return technicalAccountId;
        }

        public void setTechnicalAccountId(String technicalAccountId) {
            this.technicalAccountId = technicalAccountId;
        }

        public String getOrgId() {
            return orgId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        public void setPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        public String getPrivateKeyContent() {
            return privateKeyContent;
        }

        public void setPrivateKeyContent(String privateKeyContent) {
            this.privateKeyContent = privateKeyContent;
        }

        public List<String> getMetaScopes() {
            return metaScopes;
        }

        public void setMetaScopes(List<String> metaScopes) {
            this.metaScopes = metaScopes;
        }

        public String getImsEndpoint() {
            return imsEndpoint;
        }

        public void setImsEndpoint(String imsEndpoint) {
            this.imsEndpoint = imsEndpoint;
        }
    }

    // Getters and setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ServiceCredentials getServiceCredentials() {
        return serviceCredentials;
    }

    public void setServiceCredentials(ServiceCredentials serviceCredentials) {
        this.serviceCredentials = serviceCredentials;
    }
}
