package com.kdiachenko.aemupload;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.HttpClient5BuilderFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultSdkApiFactoryTest {

    @Mock
    private HttpClient5BuilderFactory httpClient5BuilderFactory;

    @Mock
    private ApiServerConfiguration apiServerConfiguration;

    @Mock
    private HttpClientBuilder httpClientBuilder;

    @Mock
    private CloseableHttpClient closeableHttpClient;

    private DefaultSdkApiFactory defaultSdkApiFactory;

    @BeforeEach
    void setUp() {
        when(httpClient5BuilderFactory.create()).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
        when(apiServerConfiguration.getSchema()).thenReturn("https");
        when(apiServerConfiguration.getHost()).thenReturn("example.com");
        when(apiServerConfiguration.getPort()).thenReturn("443");

        defaultSdkApiFactory = new DefaultSdkApiFactory(httpClient5BuilderFactory, apiServerConfiguration);
    }

    @Test
    void testConstructor() {
        verify(httpClient5BuilderFactory).create();
        verify(httpClientBuilder).build();

        CloseableHttpClient client = defaultSdkApiFactory.closeableHttpClient;
        assertThat(client).isEqualTo(closeableHttpClient);
    }

    @Test
    void testCreateDirectBinaryUploadApi() {
        DirectBinaryUploadApi api = defaultSdkApiFactory.createDirectBinaryUploadApi();

        assertThat(api).isNotNull();
    }

    @Test
    void testCreateAssetFolderApi() {
        AssetFolderApi api = defaultSdkApiFactory.createAssetFolderApi();

        assertThat(api).isNotNull();
    }

    @Test
    void testCreateAssetMetadataApi() {
        AssetMetadataApi api = defaultSdkApiFactory.createAssetMetadataApi();

        assertThat(api).isNotNull();
    }
}
