package com.kdia.aemupload.auth;

import com.kdia.aemupload.auth.impl.ServiceCredentialsApiAccessTokenProvider;
import com.kdia.aemupload.config.ApiAccessTokenConfiguration;
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
