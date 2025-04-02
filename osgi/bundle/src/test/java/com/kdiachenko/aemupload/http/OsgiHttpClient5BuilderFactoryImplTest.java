package com.kdiachenko.aemupload.http;

import com.kdiachenko.aemupload.stubs.HttpClient5BuilderConfiguratorStub;
import com.kdiachenko.aemupload.stubs.HttpClient5TrackerStub;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.sling.testing.mock.osgi.context.OsgiContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class OsgiHttpClient5BuilderFactoryImplTest {
    private final OsgiContextImpl osgiContext = new OsgiContextImpl();

    @Test
    void testNotInitializedHttpClient5BuilderFactory() {
        HttpClient5BuilderFactory httpClient5BuilderFactory = osgiContext.getService(HttpClient5BuilderFactory.class);

        assertNull(httpClient5BuilderFactory);
    }

    @Test
    void testHttpClient5BuilderFactoryInitializationWithCustomConfiguration() {
        osgiContext.registerService(HttpClient5Tracker.class, new HttpClient5TrackerStub());
        osgiContext.registerService(HttpClient5BuilderConfigurator.class, new HttpClient5BuilderConfiguratorStub());

        osgiContext.registerInjectActivateService(OsgiHttpClient5BuilderFactoryImpl.class, Map.of(
                "connectionRequestTimeout", 30_000,
                "responseTimeout", 15_000
        ));
        HttpClient5BuilderFactory httpClient5BuilderFactory = osgiContext.getService(HttpClient5BuilderFactory.class);

        assertNotNull(httpClient5BuilderFactory);
        assertInstanceOf(OsgiHttpClient5BuilderFactoryImpl.class, httpClient5BuilderFactory);
        OsgiHttpClient5BuilderFactoryImpl factory = (OsgiHttpClient5BuilderFactoryImpl) httpClient5BuilderFactory;
        assertEquals(30000, factory.getDefaultRequestConfig().getConnectionRequestTimeout().getDuration());
        assertEquals(TimeUnit.MILLISECONDS, factory.getDefaultRequestConfig().getConnectionRequestTimeout().getTimeUnit());
        assertEquals(15000, factory.getDefaultRequestConfig().getResponseTimeout().getDuration());
        assertEquals(TimeUnit.MILLISECONDS, factory.getDefaultRequestConfig().getResponseTimeout().getTimeUnit());
    }

    @Test
    void testHttpClient5BuilderFactoryInitializationWithDefaultConfiguration() {
        osgiContext.registerService(HttpClient5Tracker.class, new HttpClient5TrackerStub());
        osgiContext.registerService(HttpClient5BuilderConfigurator.class, new HttpClient5BuilderConfiguratorStub());

        osgiContext.registerInjectActivateService(OsgiHttpClient5BuilderFactoryImpl.class);
        HttpClient5BuilderFactory httpClient5BuilderFactory = osgiContext.getService(HttpClient5BuilderFactory.class);

        assertNotNull(httpClient5BuilderFactory);
        assertInstanceOf(OsgiHttpClient5BuilderFactoryImpl.class, httpClient5BuilderFactory);
        OsgiHttpClient5BuilderFactoryImpl factory = (OsgiHttpClient5BuilderFactoryImpl) httpClient5BuilderFactory;
        assertEquals(60000, factory.getDefaultRequestConfig().getConnectionRequestTimeout().getDuration());
        assertEquals(TimeUnit.MILLISECONDS, factory.getDefaultRequestConfig().getConnectionRequestTimeout().getTimeUnit());
        assertEquals(60000, factory.getDefaultRequestConfig().getResponseTimeout().getDuration());
    }

    @Test
    void testCreate() {
        List<CloseableHttpClient> clients = new ArrayList<>();
        osgiContext.registerService(HttpClient5Tracker.class, new HttpClient5TrackerStub() {
            @Override
            public void track(CloseableHttpClient client) {
                clients.add(client);
            }
        });
        osgiContext.registerService(HttpClient5BuilderConfigurator.class, new HttpClient5BuilderConfiguratorStub());

        osgiContext.registerInjectActivateService(OsgiHttpClient5BuilderFactoryImpl.class);
        HttpClient5BuilderFactory httpClient5BuilderFactory = osgiContext.getService(HttpClient5BuilderFactory.class);

        assertNotNull(httpClient5BuilderFactory);
        HttpClientBuilder httpClientBuilder = httpClient5BuilderFactory.create();
        assertEquals(0, clients.size());
        CloseableHttpClient createdClient = httpClientBuilder.build();
        assertEquals(1, clients.size());
        assertEquals(createdClient, clients.get(0));
    }

}
