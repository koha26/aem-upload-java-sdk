package com.kdiachenko.aemupload.auth;

import com.kdiachenko.aemupload.config.AuthConfig;

public interface AuthorizationProviderFactory {
    ApiAuthorizationProvider create(AuthConfig authConfig);
}
