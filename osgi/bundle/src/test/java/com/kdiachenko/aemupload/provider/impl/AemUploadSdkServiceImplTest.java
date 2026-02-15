package com.kdiachenko.aemupload.provider.impl;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AemUploadSdkServiceImplTest {

    @Test
    void activate_shouldCreateSdkWithAccessToken() {
        AemUploadSdkServiceImpl service = new AemUploadSdkServiceImpl();
        AemUploadSdkServiceImpl.Config config = baseConfig("accessToken");
        when(config.accessToken()).thenReturn("token");

        service.activate(config);

        assertTrue(service.isReady());
        assertNotNull(service.getSdk());
        DirectBinaryUploadApi uploadApi = service.directBinaryUploadApi();
        AssetFolderApi folderApi = service.assetFolderApi();
        AssetMetadataApi metadataApi = service.assetMetadataApi();
        assertNotNull(uploadApi);
        assertNotNull(folderApi);
        assertNotNull(metadataApi);
        assertNotNull(service.getDirectBinaryUploadApi());
        assertNotNull(service.getAssetFolderApi());
        assertNotNull(service.getAssetMetadataApi());

        service.deactivate();
    }

    @Test
    void activate_shouldHandleMissingAccessToken() {
        AemUploadSdkServiceImpl service = new AemUploadSdkServiceImpl();
        AemUploadSdkServiceImpl.Config config = baseConfig("accessToken");
        when(config.accessToken()).thenReturn(" ");

        service.activate(config);

        assertFalse(service.isReady());
        assertThrows(IllegalStateException.class, service::getSdk);
    }

    @Test
    void activate_shouldCreateSdkWithBasicAuth() {
        AemUploadSdkServiceImpl service = new AemUploadSdkServiceImpl();
        AemUploadSdkServiceImpl.Config config = baseConfig("basic");

        service.activate(config);

        assertTrue(service.isReady());
        assertNotNull(service.getSdk());

        service.deactivate();
    }

    @Test
    void activate_shouldCreateSdkWithServiceCredentials() {
        AemUploadSdkServiceImpl service = new AemUploadSdkServiceImpl();
        AemUploadSdkServiceImpl.Config config = baseConfig("serviceCredentials");
        when(config.privateKeyContent()).thenReturn("key");
        when(config.privateKeyPath()).thenReturn("");

        service.activate(config);

        assertTrue(service.isReady());
        assertNotNull(service.getSdk());

        service.deactivate();
    }

    @Test
    void activate_shouldHandleMissingServiceCredentialsKey() {
        AemUploadSdkServiceImpl service = new AemUploadSdkServiceImpl();
        AemUploadSdkServiceImpl.Config config = baseConfig("serviceCredentials");
        when(config.privateKeyContent()).thenReturn(" ");
        when(config.privateKeyPath()).thenReturn(" ");

        service.activate(config);

        assertFalse(service.isReady());
    }

    @Test
    void activate_shouldHandleUnknownAuthType() {
        AemUploadSdkServiceImpl service = new AemUploadSdkServiceImpl();
        AemUploadSdkServiceImpl.Config config = baseConfig("unknown");

        service.activate(config);

        assertFalse(service.isReady());
    }

    @Test
    void deactivate_shouldResetReady() {
        AemUploadSdkServiceImpl service = new AemUploadSdkServiceImpl();
        AemUploadSdkServiceImpl.Config config = baseConfig("basic");

        service.activate(config);
        service.deactivate();

        assertFalse(service.isReady());
    }

    @Test
    void closeQuietly_shouldHandleIOException() {
        AemUploadSdkServiceImpl service = new AemUploadSdkServiceImpl();
        Closeable closeable = () -> { throw new IOException("boom"); };

        service.closeQuietly(closeable);
    }

    private AemUploadSdkServiceImpl.Config baseConfig(String authType) {
        AemUploadSdkServiceImpl.Config config = mock(AemUploadSdkServiceImpl.Config.class);
        when(config.serverUrl()).thenReturn("https://example.com");
        when(config.authType()).thenReturn(authType);
        when(config.accessToken()).thenReturn("token");
        when(config.username()).thenReturn("user");
        when(config.password()).thenReturn("pass");
        when(config.clientId()).thenReturn("client");
        when(config.clientSecret()).thenReturn("secret");
        when(config.technicalAccountId()).thenReturn("tech");
        when(config.orgId()).thenReturn("org");
        when(config.privateKeyContent()).thenReturn("key");
        when(config.privateKeyPath()).thenReturn("");
        when(config.metaScopes()).thenReturn(new String[]{"scope"});
        when(config.imsEndpoint()).thenReturn("https://ims.example.com");
        return config;
    }
}
