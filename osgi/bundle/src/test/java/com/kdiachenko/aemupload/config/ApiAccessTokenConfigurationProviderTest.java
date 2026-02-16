package com.kdiachenko.aemupload.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiAccessTokenConfigurationProviderTest {

    @Test
    void activate_shouldPopulateConfiguration() {
        ApiAccessTokenConfigurationProvider provider = new ApiAccessTokenConfigurationProvider();
        ApiAccessTokenConfigurationProvider.Config config = mock(ApiAccessTokenConfigurationProvider.Config.class);

        when(config.localDevelopmentAccessToken()).thenReturn("token");
        when(config.imsEndpoint()).thenReturn("https://ims.example.com");
        when(config.metaScopes()).thenReturn(new String[]{"scope1", "scope2"});
        when(config.clientId()).thenReturn("client");
        when(config.clientSecret()).thenReturn("secret");
        when(config.email()).thenReturn("email@example.com");
        when(config.id()).thenReturn("id");
        when(config.org()).thenReturn("org");
        when(config.privateKeyFilePath()).thenReturn("/path/key.pem");
        when(config.privateKeyContent()).thenReturn("key");
        when(config.tokenLifeTimeInSec()).thenReturn(3600);

        provider.activate(config);

        assertEquals("https://ims.example.com", provider.getImsEndpoint());
        assertEquals("id", provider.getId());
        assertEquals("org", provider.getOrg());
        assertEquals("client", provider.getClientId());
        assertEquals("secret", provider.getClientSecret());
        assertEquals("email@example.com", provider.getEmail());
        assertEquals(List.of("scope1", "scope2"), provider.getMetaScopes());
        assertEquals("/path/key.pem", provider.getPrivateKeyFilePath());
        assertEquals("key", provider.getPrivateKeyContent());
        assertEquals(3600, provider.getTokenLifeTimeInSec());
        assertEquals("token", provider.getLocalDevelopmentAccessToken());
    }
}
