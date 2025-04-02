package com.kdiachenko.aemupload.config;

import com.kdia.aemupload.config.ApiAccessTokenConfiguration;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.osgi.context.OsgiContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class ApiAccessTokenConfigurationProviderTest {
    private final OsgiContextImpl osgiContext = new OsgiContextImpl();

    @Test
    void testNotInitializedApiAccessTokenConfiguration() {
        ApiAccessTokenConfiguration apiAccessTokenConfiguration = osgiContext.getService(ApiAccessTokenConfiguration.class);

        assertNull(apiAccessTokenConfiguration);
    }

    @Test
    void testApiAccessTokenConfigurationInitializationWithCustomConfiguration() {
        osgiContext.registerInjectActivateService(ApiAccessTokenConfigurationProvider.class, Map.of(
                "localDevelopmentAccessToken", "my local token",
                "imsEndpoint", "https://ims.adobelogin.com/ims/exchange/jwt",
                "metaScopes", new String[] {"aem_cloud_api", "aem_cloud_api2"},
                "clientId", "client 123",
                "clientSecret", "secret 456",
                "id", "id 789",
                "org", "org 111",
                "privateKeyFilePath", "/tmp/certs/private.key",
                "privateKeyContent", "privateKey content",
                "tokenLifeTimeInSec", 60
        ));

        ApiAccessTokenConfiguration configuration = osgiContext.getService(ApiAccessTokenConfiguration.class);

        assertNotNull(configuration);
        assertEquals("my local token", configuration.getLocalDevelopmentAccessToken());
        assertEquals("https://ims.adobelogin.com/ims/exchange/jwt", configuration.getImsEndpoint());
        assertEquals(List.of("aem_cloud_api", "aem_cloud_api2"), configuration.getMetaScopes());
        assertEquals("client 123", configuration.getClientId());
        assertEquals("secret 456", configuration.getClientSecret());
        assertNull(configuration.getEmail());
        assertEquals("id 789", configuration.getId());
        assertEquals("org 111", configuration.getOrg());
        assertEquals("/tmp/certs/private.key", configuration.getPrivateKeyFilePath());
        assertEquals("privateKey content", configuration.getPrivateKeyContent());
        assertEquals(60, configuration.getTokenLifeTimeInSec());
    }

    @Test
    void testApiAccessTokenConfigurationWithDefaultConfiguration() {
        osgiContext.registerInjectActivateService(ApiAccessTokenConfigurationProvider.class);

        ApiAccessTokenConfiguration configuration = osgiContext.getService(ApiAccessTokenConfiguration.class);

        assertNotNull(configuration);
        assertNull(configuration.getLocalDevelopmentAccessToken());
        assertNull(configuration.getImsEndpoint());
        assertEquals(List.of(), configuration.getMetaScopes());
        assertNull(configuration.getClientId());
        assertNull(configuration.getClientSecret());
        assertNull(configuration.getEmail());
        assertNull(configuration.getId());
        assertNull(configuration.getOrg());
        assertNull(configuration.getPrivateKeyFilePath());
        assertNull(configuration.getPrivateKeyContent());
        assertEquals(86400, configuration.getTokenLifeTimeInSec());
    }
}
