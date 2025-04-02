package com.kdiachenko.aemupload.provider.impl;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.HttpClient5BuilderFactory;
import com.kdiachenko.aemupload.provider.SdkApiProvider;
import com.kdiachenko.aemupload.stubs.ApiServerConfigurationStub;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.sling.testing.mock.osgi.context.OsgiContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class SdkApiProviderImplTest {
    private final OsgiContextImpl osgiContext = new OsgiContextImpl();

    @Test
    void testNotInitializedSdkApiProvider() {
        SdkApiProvider sdkApiProvider = osgiContext.getService(SdkApiProvider.class);

        assertNull(sdkApiProvider);
    }

    @Test
    void testSdkApiProviderInitialization() {
        osgiContext.registerService(ApiServerConfiguration.class, ApiServerConfigurationStub.builder().host("localhost").build());
        osgiContext.registerService(HttpClient5BuilderFactory.class, HttpClientBuilder::create);

        osgiContext.registerInjectActivateService(SdkApiProviderImpl.class);
        SdkApiProvider sdkApiProvider = osgiContext.getService(SdkApiProvider.class);

        assertNotNull(sdkApiProvider);
        assertNotNull(sdkApiProvider.getDirectBinaryUploadApi());
        assertNotNull(sdkApiProvider.getAssetFolderApi());
        assertNotNull(sdkApiProvider.getAssetMetadataApi());
    }
}
