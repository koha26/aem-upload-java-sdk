package com.kdiachenko.aemupload.config;

/**
 * Marker interface for authentication configurations.
 * Implementations provide different authentication strategies.
 *
 * @see AccessTokenAuthConfig
 * @see ServiceCredentialsAuthConfig
 * @see BasicAuthConfig
 */
public interface AuthConfig {

    /**
     * Returns a descriptive name for this authentication type.
     *
     * @return the authentication type name
     */
    String getAuthType();
}
