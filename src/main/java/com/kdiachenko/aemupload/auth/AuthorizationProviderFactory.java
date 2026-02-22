package com.kdiachenko.aemupload.auth;

import com.kdiachenko.aemupload.config.AuthConfig;

/**
 * Factory for creating {@link ApiAuthorizationProvider} instances
 * based on a given {@link AuthConfig}.
 */
public interface AuthorizationProviderFactory {
    /**
     * Creates an authorization provider for the given authentication configuration.
     *
     * @param authConfig authentication configuration
     * @return provider that can apply authorization to requests
     */
    ApiAuthorizationProvider create(AuthConfig authConfig);
}
