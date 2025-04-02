package com.kdiachenko.aemupload.http;

import com.kdia.aemupload.config.ApiAccessTokenConfiguration;
import com.kdia.aemupload.http.HttpClient5BuilderConfigurator;
import com.kdiachenko.aemupload.auth.OsgiDefaultApiAccessTokenProviderFactory;
import com.kdiachenko.aemupload.stubs.ApiAccessTokenConfigurationStub;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.sling.testing.mock.osgi.context.OsgiContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class OsgiHttpClient5BuilderConfiguratorTest {
    private final OsgiContextImpl osgiContext = new OsgiContextImpl();

    @Test
    void testConfiguratorInitializationWhenFactoryIsMissing() {
        HttpClient5BuilderConfigurator configurator = osgiContext.getService(HttpClient5BuilderConfigurator.class);

        assertNull(configurator);
    }

    @Test
    void testConfiguratorInitializationWhenFactoryIsPresent() {
        ApiAccessTokenConfigurationStub apiAccessTokenConfigurationStub =
                ApiAccessTokenConfigurationStub.builder().localDevelopmentAccessToken("my_local_token").build();
        osgiContext.registerService(ApiAccessTokenConfiguration.class, apiAccessTokenConfigurationStub);
        osgiContext.registerInjectActivateService(OsgiDefaultApiAccessTokenProviderFactory.class);
        osgiContext.registerInjectActivateService(OsgiHttpClient5BuilderConfigurator.class);

        HttpClient5BuilderConfigurator configurator = osgiContext.getService(HttpClient5BuilderConfigurator.class);

        assertNotNull(configurator);
        assertNotNull(configurator.configure(HttpClients.custom()));
    }
}
