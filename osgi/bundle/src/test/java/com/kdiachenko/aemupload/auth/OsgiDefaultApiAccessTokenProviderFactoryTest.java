package com.kdiachenko.aemupload.auth;

import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import com.kdiachenko.aemupload.stubs.ApiAccessTokenConfigurationStub;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.osgi.context.OsgiContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class OsgiDefaultApiAccessTokenProviderFactoryTest {

    private final OsgiContextImpl osgiContext = new OsgiContextImpl();

    @Test
    void testFactoryInitializationWhenApiTokenConfigurationIsMissing() {
        ApiAccessTokenProviderFactory service = osgiContext.getService(ApiAccessTokenProviderFactory.class);

        assertNull(service);
    }

    @Test
    void testFactoryInitializationWhenApiTokenConfigurationIsPresent() {
        ApiAccessTokenConfigurationStub apiAccessTokenConfigurationStub =
                ApiAccessTokenConfigurationStub.builder().localDevelopmentAccessToken("my_local_token").build();
        osgiContext.registerService(ApiAccessTokenConfiguration.class, apiAccessTokenConfigurationStub);
        osgiContext.registerInjectActivateService(OsgiDefaultApiAccessTokenProviderFactory.class);

        ApiAccessTokenProviderFactory service = osgiContext.getService(ApiAccessTokenProviderFactory.class);

        assertNotNull(service);
        assertNotNull(service.create());
    }

}
