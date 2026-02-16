package com.kdiachenko.aemupload.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiServerConfigurationProviderTest {

    @Test
    void activate_shouldPopulateConfiguration() {
        ApiServerConfigurationProvider provider = new ApiServerConfigurationProvider();
        ApiServerConfigurationProvider.Config config = mock(ApiServerConfigurationProvider.Config.class);

        when(config.serverSchema()).thenReturn("https");
        when(config.serverHost()).thenReturn("example.com");
        when(config.serverPort()).thenReturn("4502");

        provider.activate(config);

        assertEquals("https", provider.getSchema());
        assertEquals("example.com", provider.getHost());
        assertEquals("4502", provider.getPort());
    }
}
