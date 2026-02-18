package com.kdiachenko.aemupload.api.impl;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.entity.HttpContexts;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.AssetElement;
import com.kdiachenko.aemupload.exception.SdkError;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssetFolderApiImplTest {

    private static final String HOST_URL = "https://example.com";
    private static final String FOLDER_PATH = "/content/dam/test";
    private static final String NORMALIZED_FOLDER_PATH = "/api/assets/test";

    @Mock
    private ApiHttpClient apiHttpClient;

    @Mock
    private ApiServerConfiguration apiServerConfiguration;

    @Mock
    private PathNormalizer pathNormalizer;

    @Mock
    private AssetElement assetElement;

    @Captor
    private ArgumentCaptor<ApiHttpEntity<Map<String, Object>>> httpEntityCaptor;

    private AssetFolderApiImpl assetFolderApi;

    @BeforeEach
    void setUp() {
        when(apiServerConfiguration.getHostUrl()).thenReturn(HOST_URL);
        when(pathNormalizer.normalize(FOLDER_PATH)).thenReturn(NORMALIZED_FOLDER_PATH);

        assetFolderApi = new AssetFolderApiImpl(apiHttpClient, apiServerConfiguration, pathNormalizer);
    }

    @Test
    @DisplayName("getFolder should make GET request and return mapped response")
    void getFolder_shouldMakeGetRequestAndReturnMappedResponse() {
        // Arrange
        ApiHttpResponse<AssetElement> assetElementResponse = ApiHttpResponse.<AssetElement>builder()
                .status(200)
                .body(assetElement)
                .build();
        when(apiHttpClient.get(eq(HOST_URL + NORMALIZED_FOLDER_PATH), eq(HttpContexts.AUTHORIZED), eq(AssetElement.class)))
                .thenReturn(assetElementResponse);

        // Act
        AssetApiResponse<AssetElement> response = assetFolderApi.getFolder(FOLDER_PATH);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(assetElement);
        verify(apiHttpClient).get(eq(HOST_URL + NORMALIZED_FOLDER_PATH), eq(HttpContexts.AUTHORIZED), eq(AssetElement.class));
    }

    @Test
    @DisplayName("createFolder should make POST request with default properties and return mapped response")
    void createFolder_shouldMakePostRequestWithDefaultPropertiesAndReturnMappedResponse() {
        // Arrange
        ApiHttpResponse<Void> voidResponse = ApiHttpResponse.<Void>builder().status(200).build();
        when(apiHttpClient.post(eq(HOST_URL + NORMALIZED_FOLDER_PATH), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(Void.class)))
                .thenReturn(voidResponse);

        // Act
        AssetApiResponse<Void> response = assetFolderApi.createFolder(FOLDER_PATH);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        verify(apiHttpClient).post(eq(HOST_URL + NORMALIZED_FOLDER_PATH), httpEntityCaptor.capture(), eq(HttpContexts.AUTHORIZED), eq(Void.class));

        Map<String, Object> formData = (Map<String, Object>) httpEntityCaptor.getValue().getBody();
        assertThat(formData).containsEntry("class", "assetFolder");
        Map<String, String> properties = (Map<String, String>) formData.get("properties");
        assertThat(properties).containsEntry("title", "test");
    }

    @Test
    @DisplayName("createFolder with properties should make POST request with custom properties and return mapped response")
    void createFolderWithProperties_shouldMakePostRequestWithCustomPropertiesAndReturnMappedResponse() {
        // Arrange
        Map<String, String> customProperties = Map.of("title", "Custom Title", "description", "Custom Description");
        ApiHttpResponse<Void> voidResponse = ApiHttpResponse.<Void>builder().status(200).build();
        when(apiHttpClient.post(eq(HOST_URL + NORMALIZED_FOLDER_PATH), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(Void.class)))
                .thenReturn(voidResponse);

        // Act
        AssetApiResponse<Void> response = assetFolderApi.createFolder(FOLDER_PATH, customProperties);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        verify(apiHttpClient).post(eq(HOST_URL + NORMALIZED_FOLDER_PATH), httpEntityCaptor.capture(), eq(HttpContexts.AUTHORIZED), eq(Void.class));

        Map<String, Object> formData = (Map<String, Object>) httpEntityCaptor.getValue().getBody();
        assertThat(formData).containsEntry("class", "assetFolder");
        Map<String, String> properties = (Map<String, String>) formData.get("properties");
        assertThat(properties).isEqualTo(customProperties);
    }

    @Test
    @DisplayName("getFolder should handle error response")
    void getFolder_shouldHandleErrorResponse() {
        // Arrange
        ApiHttpResponse<AssetElement> assetElementResponse = ApiHttpResponse.<AssetElement>builder()
                .status(500)
                .errorMessage("Error message")
                .build();
        when(apiHttpClient.get(eq(HOST_URL + NORMALIZED_FOLDER_PATH), eq(HttpContexts.AUTHORIZED), eq(AssetElement.class)))
                .thenReturn(assetElementResponse);

        // Act
        AssetApiResponse<AssetElement> response = assetFolderApi.getFolder(FOLDER_PATH);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .isEqualTo("Error message");
    }

    @Test
    @DisplayName("createFolder should handle error response")
    void createFolder_shouldHandleErrorResponse() {
        // Arrange
        ApiHttpResponse<Void> voidResponse = ApiHttpResponse.<Void>builder()
                .status(500)
                .errorMessage("Error message")
                .build();
        when(apiHttpClient.post(eq(HOST_URL + NORMALIZED_FOLDER_PATH), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(Void.class)))
                .thenReturn(voidResponse);

        // Act
        AssetApiResponse<Void> response = assetFolderApi.createFolder(FOLDER_PATH);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .isEqualTo("Error message");
    }

    @Test
    @DisplayName("getFolder should validate blank folder")
    void getFolder_shouldValidateBlankFolder() {
        AssetApiResponse<AssetElement> response = assetFolderApi.getFolder("  ");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getHttpStatus)
                .isEqualTo(400);
    }

    @Test
    @DisplayName("createFolder should validate null properties")
    void createFolder_shouldValidateNullProperties() {
        AssetApiResponse<Void> response = assetFolderApi.createFolder(FOLDER_PATH, null);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .isEqualTo("properties must not be null");
    }

    @Test
    @DisplayName("createFolder should validate blank folder")
    void createFolder_shouldValidateBlankFolder() {
        AssetApiResponse<Void> response = assetFolderApi.createFolder(" ");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getHttpStatus)
                .isEqualTo(400);
    }

    @Test
    @DisplayName("createFolder with properties should validate blank folder")
    void createFolderWithProperties_shouldValidateBlankFolder() {
        AssetApiResponse<Void> response = assetFolderApi.createFolder(" ", Map.of("title", "x"));

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getHttpStatus)
                .isEqualTo(400);
    }

    @Test
    @DisplayName("createFolder should use full folder name when no slash exists")
    void createFolder_shouldUseFolderAsTitleWhenNoSlash() {
        String simpleFolder = "folder";
        ApiHttpResponse<Void> voidResponse = ApiHttpResponse.<Void>builder().status(200).build();
        when(pathNormalizer.normalize(simpleFolder)).thenReturn("/" + simpleFolder);
        when(apiHttpClient.post(eq(HOST_URL + "/" + simpleFolder), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(Void.class)))
                .thenReturn(voidResponse);

        AssetApiResponse<Void> response = assetFolderApi.createFolder(simpleFolder);

        assertThat(response.isSuccess()).isTrue();
        verify(apiHttpClient).post(eq(HOST_URL + "/" + simpleFolder), httpEntityCaptor.capture(),
                eq(HttpContexts.AUTHORIZED), eq(Void.class));
        Map<String, Object> formData = (Map<String, Object>) httpEntityCaptor.getValue().getBody();
        Map<String, String> properties = (Map<String, String>) formData.get("properties");
        assertThat(properties).containsEntry("title", simpleFolder);
    }
}
