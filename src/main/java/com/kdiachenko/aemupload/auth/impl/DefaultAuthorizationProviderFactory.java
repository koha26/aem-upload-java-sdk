package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.auth.ApiAuthorizationProvider;
import com.kdiachenko.aemupload.auth.AuthorizationProviderFactory;
import com.kdiachenko.aemupload.config.AccessTokenAuthConfig;
import com.kdiachenko.aemupload.config.AuthConfig;
import com.kdiachenko.aemupload.config.BasicAuthConfig;
import com.kdiachenko.aemupload.config.ServiceCredentialsAuthConfig;

public class DefaultAuthorizationProviderFactory implements AuthorizationProviderFactory {
    @Override
    public ApiAuthorizationProvider create(AuthConfig authConfig) {
        if (authConfig instanceof AccessTokenAuthConfig) {
            AccessTokenAuthConfig tokenAuth = (AccessTokenAuthConfig) authConfig;
            ApiAccessTokenProvider tokenProvider = tokenAuth::getAccessToken;
            return new BearerTokenAuthorizationProvider(tokenProvider);
        }
        if (authConfig instanceof ServiceCredentialsAuthConfig) {
            ServiceCredentialsAuthConfig serviceAuth = (ServiceCredentialsAuthConfig) authConfig;
            ApiAccessTokenProvider tokenProvider = new ServiceCredentialsApiAccessTokenProvider(serviceAuth);
            return new BearerTokenAuthorizationProvider(tokenProvider);
        }
        if (authConfig instanceof BasicAuthConfig) {
            BasicAuthConfig basicAuth = (BasicAuthConfig) authConfig;
            return new BasicAuthorizationProvider(basicAuth.getUsername(), basicAuth.getPassword());
        }
        throw new IllegalArgumentException("Unsupported authentication configuration: "
                + authConfig.getClass().getName());
    }
}
