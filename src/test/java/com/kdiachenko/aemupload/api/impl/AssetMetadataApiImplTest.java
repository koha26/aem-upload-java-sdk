package com.kdiachenko.aemupload.api.impl;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.entity.HttpContexts;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.DamAsset;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssetMetadataApiImplTest {

    private static final String HOST_URL = "https://example.com";
    private static final String ASSET_PATH = "/content/dam/test/asset.jpg";
    private static final String NORMALIZED_ASSET_PATH = "/api/assets/test/asset.jpg";
    private static final String METADATA_URL = HOST_URL + ASSET_PATH + "/jcr:content/metadata.json";

    @Mock
    private ApiHttpClient apiHttpClient;

    @Mock
    private ApiServerConfiguration apiServerConfiguration;

    @Mock
    private PathNormalizer pathNormalizer;

    @Mock
    private ApiHttpResponse<DamAsset> damAssetResponse;

    @Mock
    private ApiHttpResponse<Void> voidResponse;

    @Mock
    private DamAsset damAsset;

    @Captor
    private ArgumentCaptor<ApiHttpEntity<Map<String, Object>>> httpEntityCaptor;

    private AssetMetadataApiImpl assetMetadataApi;

    @BeforeEach
    void setUp() {
        when(apiServerConfiguration.getHostUrl()).thenReturn(HOST_URL);
        when(pathNormalizer.normalize(ASSET_PATH)).thenReturn(NORMALIZED_ASSET_PATH);

        // Setup default behavior for mock responses
        when(damAssetResponse.isSuccess()).thenReturn(true);
        when(damAssetResponse.getBody()).thenReturn(damAsset);
        when(damAssetResponse.getErrorMessage()).thenReturn(null);

        when(voidResponse.isSuccess()).thenReturn(true);
        when(voidResponse.getBody()).thenReturn(null);
        when(voidResponse.getErrorMessage()).thenReturn(null);

        // Setup default behavior for apiHttpClient
        doReturn(damAssetResponse).when(apiHttpClient).get(any(), any(), any());
        doReturn(voidResponse).when(apiHttpClient).put(any(), any(), any(), any());
        doReturn(voidResponse).when(apiHttpClient).post(any(), any(), any(), any());

        assetMetadataApi = new AssetMetadataApiImpl(apiHttpClient, apiServerConfiguration, pathNormalizer);
    }

    @Test
    @DisplayName("getAssetMetadata should make GET request and return mapped response")
    void getAssetMetadata_shouldMakeGetRequestAndReturnMappedResponse() {
        // Arrange
        String metadataUrl = HOST_URL + ASSET_PATH + "/jcr:content/metadata.json";
        when(apiHttpClient.get(eq(metadataUrl), eq(HttpContexts.AUTHORIZED), eq(DamAsset.class)))
                .thenReturn(damAssetResponse);

        // Act
        AssetApiResponse<DamAsset> response = assetMetadataApi.getAssetMetadata(ASSET_PATH);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(damAsset);
        verify(apiHttpClient).get(eq(metadataUrl), eq(HttpContexts.AUTHORIZED), eq(DamAsset.class));
    }

    @Test
    @DisplayName("updateAssetMetadata should make PUT request and return mapped response")
    void updateAssetMetadata_shouldMakePutRequestAndReturnMappedResponse() {
        // Arrange
        Map<String, String> metadata = Map.of("dc:title", "New Title", "dc:description", "New Description");
        when(apiHttpClient.put(eq(HOST_URL + NORMALIZED_ASSET_PATH), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(Void.class)))
                .thenReturn(voidResponse);

        // Act
        AssetApiResponse<Void> response = assetMetadataApi.updateAssetMetadata(ASSET_PATH, metadata);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        verify(apiHttpClient).put(eq(HOST_URL + NORMALIZED_ASSET_PATH), httpEntityCaptor.capture(), eq(HttpContexts.AUTHORIZED), eq(Void.class));

        Map<String, Object> formData = (Map<String, Object>) httpEntityCaptor.getValue().getBody();
        assertThat(formData).containsEntry("class", "asset");
        Map<String, String> properties = (Map<String, String>) formData.get("properties");
        assertThat(properties).isEqualTo(metadata);
    }

    @Test
    @DisplayName("deleteAsset should make POST request and return mapped response")
    void deleteAsset_shouldMakePostRequestAndReturnMappedResponse() {
        // Arrange
        when(apiHttpClient.post(eq(HOST_URL + NORMALIZED_ASSET_PATH), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(Void.class)))
                .thenReturn(voidResponse);

        // Act
        AssetApiResponse<Void> response = assetMetadataApi.deleteAsset(ASSET_PATH);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        verify(apiHttpClient).post(eq(HOST_URL + NORMALIZED_ASSET_PATH), httpEntityCaptor.capture(), eq(HttpContexts.AUTHORIZED), eq(Void.class));

        Map<String, Object> properties = (Map<String, Object>) httpEntityCaptor.getValue().getBody();
        assertThat(properties).containsEntry(":operation", "delete");
    }

    @Test
    @DisplayName("getAssetMetadata should handle error response")
    void getAssetMetadata_shouldHandleErrorResponse() {
        // Arrange
        String metadataUrl = HOST_URL + ASSET_PATH + "/jcr:content/metadata.json";
        when(apiHttpClient.get(eq(metadataUrl), eq(HttpContexts.AUTHORIZED), eq(DamAsset.class)))
                .thenReturn(damAssetResponse);
        when(damAssetResponse.isSuccess()).thenReturn(false);
        when(damAssetResponse.getErrorMessage()).thenReturn("Error message");

        // Act
        AssetApiResponse<DamAsset> response = assetMetadataApi.getAssetMetadata(ASSET_PATH);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).isEqualTo("Error message");
    }

    @Test
    @DisplayName("updateAssetMetadata should handle error response")
    void updateAssetMetadata_shouldHandleErrorResponse() {
        // Arrange
        Map<String, String> metadata = Map.of("dc:title", "New Title");
        when(apiHttpClient.put(eq(HOST_URL + NORMALIZED_ASSET_PATH), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(Void.class)))
                .thenReturn(voidResponse);
        when(voidResponse.isSuccess()).thenReturn(false);
        when(voidResponse.getErrorMessage()).thenReturn("Error message");

        // Act
        AssetApiResponse<Void> response = assetMetadataApi.updateAssetMetadata(ASSET_PATH, metadata);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).isEqualTo("Error message");
    }

    @Test
    @DisplayName("deleteAsset should handle error response")
    void deleteAsset_shouldHandleErrorResponse() {
        // Arrange
        when(apiHttpClient.post(eq(HOST_URL + NORMALIZED_ASSET_PATH), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(Void.class)))
                .thenReturn(voidResponse);
        when(voidResponse.isSuccess()).thenReturn(false);
        when(voidResponse.getErrorMessage()).thenReturn("Error message");

        // Act
        AssetApiResponse<Void> response = assetMetadataApi.deleteAsset(ASSET_PATH);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).isEqualTo("Error message");
    }
}
