package com.kdiachenko.aemupload.auth;

import com.kdiachenko.aemupload.auth.impl.ServiceCredentialsApiAccessTokenProvider;
import com.kdiachenko.aemupload.common.ApiAccessTokenConfigurationStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultApiAccessTokenProviderFactoryTest {

    @Test
    void shouldCreateLocalDevelopmentApiAccessTokenProvider() {
        ApiAccessTokenConfigurationStub config = ApiAccessTokenConfigurationStub.builder()
                .localDevelopmentAccessToken("localDevelopmentAccessToken")
                .build();
        DefaultApiAccessTokenProviderFactory factory = new DefaultApiAccessTokenProviderFactory(config);

        ApiAccessTokenProvider apiAccessTokenProvider = factory.create();
        assertNotNull(apiAccessTokenProvider);
        assertEquals("localDevelopmentAccessToken", apiAccessTokenProvider.getAccessToken());
    }

    @Test
    void shouldCreateServiceCredentialsApiAccessTokenProvider() {
        ApiAccessTokenConfigurationStub config = ApiAccessTokenConfigurationStub.builder().build();
        DefaultApiAccessTokenProviderFactory factory = new DefaultApiAccessTokenProviderFactory(config);

        ApiAccessTokenProvider apiAccessTokenProvider = factory.create();
        assertNotNull(apiAccessTokenProvider);
        assertInstanceOf(ServiceCredentialsApiAccessTokenProvider.class, apiAccessTokenProvider);
    }
}
