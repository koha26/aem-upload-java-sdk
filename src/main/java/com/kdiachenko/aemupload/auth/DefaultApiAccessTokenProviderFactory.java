package com.kdiachenko.aemupload.auth;

import com.kdiachenko.aemupload.auth.impl.ServiceCredentialsApiAccessTokenProvider;
import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import org.apache.commons.lang3.StringUtils;

public class DefaultApiAccessTokenProviderFactory implements ApiAccessTokenProviderFactory {
    private final ApiAccessTokenConfiguration apiAccessTokenConfiguration;

    public DefaultApiAccessTokenProviderFactory(ApiAccessTokenConfiguration apiAccessTokenConfiguration) {
        this.apiAccessTokenConfiguration = apiAccessTokenConfiguration;
    }

    @Override
    public ApiAccessTokenProvider create() {
        if (StringUtils.isNotEmpty(apiAccessTokenConfiguration.getLocalDevelopmentAccessToken())) {
            return apiAccessTokenConfiguration::getLocalDevelopmentAccessToken;
        }
        return new ServiceCredentialsApiAccessTokenProvider(apiAccessTokenConfiguration);
    }
}
