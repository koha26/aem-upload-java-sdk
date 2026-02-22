package com.kdiachenko.aemupload.http.impl;

import com.kdiachenko.aemupload.http.HttpClient5BuilderConfigurator;
import com.kdiachenko.aemupload.http.HttpClient5Tracker;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OsgiHttpClient5BuilderFactoryImplTest {

    @Test
    void activate_shouldConfigureDefaultRequestConfig() {
        HttpClient5BuilderConfigurator configurator = mock(HttpClient5BuilderConfigurator.class);
        HttpClient5Tracker tracker = mock(HttpClient5Tracker.class);
        when(configurator.configure(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OsgiHttpClient5BuilderFactoryImpl factory = new OsgiHttpClient5BuilderFactoryImpl(tracker);
        OsgiHttpClient5BuilderFactoryImpl.Config config = mock(OsgiHttpClient5BuilderFactoryImpl.Config.class);
        when(config.connectionRequestTimeout()).thenReturn(1234);
        when(config.responseTimeout()).thenReturn(4321);

        factory.activate(config);

        RequestConfig requestConfig = factory.getDefaultRequestConfig();
        assertNotNull(requestConfig);
        assertEquals(1234, requestConfig.getConnectionRequestTimeout().toMilliseconds());
        assertEquals(4321, requestConfig.getResponseTimeout().toMilliseconds());
    }

    @Test
    void create_shouldApplyConfiguratorAndTrackClient() throws Exception {
        HttpClient5Tracker tracker = mock(HttpClient5Tracker.class);

        OsgiHttpClient5BuilderFactoryImpl factory = new OsgiHttpClient5BuilderFactoryImpl(tracker);
        OsgiHttpClient5BuilderFactoryImpl.Config config = mock(OsgiHttpClient5BuilderFactoryImpl.Config.class);
        when(config.connectionRequestTimeout()).thenReturn(1000);
        when(config.responseTimeout()).thenReturn(1000);
        factory.activate(config);

        HttpClientBuilder builder = factory.create();
        assertNotNull(builder);

        try (CloseableHttpClient client = builder.build()) {
            verify(tracker).track(client);
        }
    }
}
