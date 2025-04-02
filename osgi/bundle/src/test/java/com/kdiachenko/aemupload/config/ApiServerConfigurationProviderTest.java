package com.kdiachenko.aemupload.config;

import com.kdia.aemupload.config.ApiServerConfiguration;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.osgi.context.OsgiContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class ApiServerConfigurationProviderTest {

    private final OsgiContextImpl osgiContext = new OsgiContextImpl();

    @Test
    void testNotInitializedApiServerConfiguration() {
        ApiServerConfiguration apiServerConfiguration = osgiContext.getService(ApiServerConfiguration.class);

        assertNull(apiServerConfiguration);
    }

    @Test
    void testApiServerConfigurationInitializationWithCustomConfiguration() {
        osgiContext.registerInjectActivateService(ApiServerConfigurationProvider.class, Map.of(
                "serverSchema", "https",
                "serverHost", "my.aem.host",
                "serverPort", ""
        ));

        ApiServerConfiguration configuration = osgiContext.getService(ApiServerConfiguration.class);

        assertNotNull(configuration);
        assertEquals("https", configuration.getSchema());
        assertEquals("my.aem.host", configuration.getHost());
        assertEquals("", configuration.getPort());
    }

    @Test
    void testApiServerConfigurationInitializationWithDefaultConfiguration() {
        osgiContext.registerInjectActivateService(ApiServerConfigurationProvider.class);

        ApiServerConfiguration configuration = osgiContext.getService(ApiServerConfiguration.class);

        assertNotNull(configuration);
        assertEquals("https", configuration.getSchema());
        assertEquals("localhost", configuration.getHost());
        assertEquals("4502", configuration.getPort());
    }
}
