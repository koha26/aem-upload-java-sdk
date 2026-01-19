package com.kdiachenko.aemupload.api.impl;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.options.CompleteBinaryUploadOptions;
import com.kdiachenko.aemupload.options.CompleteUploadResponse;
import com.kdiachenko.aemupload.options.InitiateBinaryUploadOptions;
import com.kdiachenko.aemupload.options.InitiateUploadResponse;
import com.kdiachenko.aemupload.options.UploadBinaryOptions;
import com.kdiachenko.aemupload.options.UploadBinaryResponse;
import com.kdiachenko.aemupload.utils.FileSplitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.kdiachenko.aemupload.http.client.ApiHttpClient.AUTHORIZABLE_API_REQUEST;
import static org.apache.hc.core5.http.ContentType.APPLICATION_FORM_URLENCODED;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DirectBinaryUploadApiImplTest {

    private static final String HOST_URL = "https://example.com";
    private static final String DAM_ASSET_FOLDER = "/content/dam/test";
    private static final String NORMALIZED_DAM_ASSET_FOLDER = "/api/assets/test";
    private static final String FILE_NAME = "test.jpg";
    private static final long FILE_SIZE = 1024L;
    private static final String CONTENT_TYPE_VALUE = "image/jpeg";
    private static final String UPLOAD_TOKEN = "upload-token-123";
    private static final String COMPLETE_URI = "/content/dam/test.completeUpload.json";

    @TempDir
    Path tempDir;

    @Mock
    private ApiHttpClient apiHttpClient;

    @Mock
    private ApiServerConfiguration apiServerConfiguration;

    @Mock
    private ApiHttpResponse<InitiateUploadResponse> initiateUploadResponse;

    @Mock
    private ApiHttpResponse<Void> uploadPartResponse;

    @Mock
    private ApiHttpResponse<CompleteUploadResponse> completeUploadResponse;

    @Mock
    private InitiateUploadResponse initiateUploadResponseBody;

    @Mock
    private CompleteUploadResponse completeUploadResponseBody;

    @Captor
    private ArgumentCaptor<ApiHttpEntity<?>> httpEntityCaptor;

    private DirectBinaryUploadApiImpl directBinaryUploadApi;
    private Path binaryFile;
    private List<URI> uploadURIs;

    @BeforeEach
    void setUp() throws IOException {
        when(apiServerConfiguration.getHostUrl()).thenReturn(HOST_URL);

        // Create a test binary file
        binaryFile = tempDir.resolve("test.jpg");
        Files.write(binaryFile, "test binary data".getBytes());

        // Setup mock URIs
        uploadURIs = Arrays.asList(
            URI.create("https://example.com/part1"),
            URI.create("https://example.com/part2")
        );

        // Setup default behavior for mock responses
        when(initiateUploadResponse.isSuccess()).thenReturn(true);
        when(initiateUploadResponse.getBody()).thenReturn(initiateUploadResponseBody);
        when(initiateUploadResponse.getErrorMessage()).thenReturn(null);
        //when(initiateUploadResponseBody.getUploadURIs()).thenReturn(uploadURIs);
        when(initiateUploadResponseBody.getCompleteURI()).thenReturn(COMPLETE_URI);
        //when(initiateUploadResponseBody.getUploadToken()).thenReturn(UPLOAD_TOKEN);

        when(uploadPartResponse.isSuccess()).thenReturn(true);
        when(uploadPartResponse.getBody()).thenReturn(null);
        when(uploadPartResponse.getErrorMessage()).thenReturn(null);

        when(completeUploadResponse.isSuccess()).thenReturn(true);
        when(completeUploadResponse.getBody()).thenReturn(completeUploadResponseBody);
        when(completeUploadResponse.getErrorMessage()).thenReturn(null);

        // Setup default behavior for apiHttpClient
        doReturn(initiateUploadResponse).when(apiHttpClient).post(anyString(), any(ApiHttpEntity.class), eq(AUTHORIZABLE_API_REQUEST), eq(InitiateUploadResponse.class));
        doReturn(uploadPartResponse).when(apiHttpClient).put(anyString(), any(ApiHttpEntity.class), eq(Void.class));
        doReturn(completeUploadResponse).when(apiHttpClient).post(anyString(), any(ApiHttpEntity.class), eq(AUTHORIZABLE_API_REQUEST), eq(CompleteUploadResponse.class));

        directBinaryUploadApi = new DirectBinaryUploadApiImpl(apiHttpClient, apiServerConfiguration);
    }

    @Test
    @DisplayName("initiateUpload should make POST request and return mapped response")
    void initiateUpload_shouldMakePostRequestAndReturnMappedResponse() {
        // Arrange
        InitiateBinaryUploadOptions options = InitiateBinaryUploadOptions.builder()
                .damAssetFolder(DAM_ASSET_FOLDER)
                .fileName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .build();

        String expectedUrl = HOST_URL + NORMALIZED_DAM_ASSET_FOLDER + ".initiateUpload.json";

        // Act
        AssetApiResponse<InitiateUploadResponse> response = directBinaryUploadApi.initiateUpload(options);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(initiateUploadResponseBody);
        verify(apiHttpClient).post(eq(expectedUrl), httpEntityCaptor.capture(), eq(AUTHORIZABLE_API_REQUEST), eq(InitiateUploadResponse.class));

        Map<String, Object> formData = (Map<String, Object>) httpEntityCaptor.getValue().getBody();
        assertThat(formData).containsEntry("fileName", FILE_NAME);
        assertThat(formData).containsEntry("fileSize", FILE_SIZE);

        Map<String, String> headers = httpEntityCaptor.getValue().getHeaders();
        assertThat(headers).containsEntry(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString());
    }

    @Test
    @DisplayName("uploadBinary should split file, upload parts and return success response")
    void uploadBinary_shouldSplitFileUploadPartsAndReturnSuccessResponse() throws IOException {
        // Arrange
        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(binaryFile)
                .contentType(CONTENT_TYPE_VALUE)
                .uploadURIs(uploadURIs)
                .maxPartSize(512L)
                .build();

        Path part1 = tempDir.resolve("part1");
        Path part2 = tempDir.resolve("part2");
        Files.write(part1, "part1 data".getBytes());
        Files.write(part2, "part2 data".getBytes());
        List<Path> parts = Arrays.asList(part1, part2);

        try (MockedStatic<FileSplitUtil> fileSplitUtilMock = mockStatic(FileSplitUtil.class);
             MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            fileSplitUtilMock.when(() -> FileSplitUtil.splitFile(eq(binaryFile), anyLong()))
                    .thenReturn(parts);

            // Mock Files.newInputStream
            InputStream mockInputStream1 = Files.newInputStream(part1);
            InputStream mockInputStream2 = Files.newInputStream(part2);
            filesMock.when(() -> Files.newInputStream(part1)).thenReturn(mockInputStream1);
            filesMock.when(() -> Files.newInputStream(part2)).thenReturn(mockInputStream2);

            // Mock Files.delete
            filesMock.when(() -> Files.delete(any(Path.class))).thenReturn(true);

            // Act
            AssetApiResponse<UploadBinaryResponse> response = directBinaryUploadApi.uploadBinary(options);

            // Assert
            assertThat(response.isSuccess()).isTrue();
            //assertThat(response.getBody().getPartsCount()).isEqualTo(2);

            // Verify put was called for each part
            verify(apiHttpClient).put(eq("https://example.com/part1"), any(ApiHttpEntity.class), eq(Void.class));
            verify(apiHttpClient).put(eq("https://example.com/part2"), any(ApiHttpEntity.class), eq(Void.class));

            // Verify Files.delete was called for each part
            filesMock.verify(() -> Files.delete(part1));
            filesMock.verify(() -> Files.delete(part2));
        }
    }

    @Test
    @DisplayName("completeUpload should make POST request and return mapped response")
    void completeUpload_shouldMakePostRequestAndReturnMappedResponse() {
        // Arrange
        CompleteBinaryUploadOptions options = CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(CONTENT_TYPE_VALUE)
                .uploadToken(UPLOAD_TOKEN)
                .createVersion(true)
                .replace(false)
                .versionComment("Test version")
                .uploadDuration(1000L)
                .fileSize(FILE_SIZE)
                .build();

        String expectedUrl = HOST_URL + COMPLETE_URI;

        // Act
        AssetApiResponse<CompleteUploadResponse> response = directBinaryUploadApi.completeUpload(options);

        // Assert
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(completeUploadResponseBody);
        verify(apiHttpClient).post(eq(expectedUrl), httpEntityCaptor.capture(), eq(AUTHORIZABLE_API_REQUEST), eq(CompleteUploadResponse.class));

        Map<String, String> formData = (Map<String, String>) httpEntityCaptor.getValue().getBody();
        assertThat(formData).containsEntry("fileName", FILE_NAME);
        assertThat(formData).containsEntry("mimeType", CONTENT_TYPE_VALUE);
        assertThat(formData).containsEntry("uploadToken", UPLOAD_TOKEN);
        assertThat(formData).containsEntry("createVersion", "true");
        assertThat(formData).containsEntry("replace", "false");
        assertThat(formData).containsEntry("versionComment", "Test version");
        assertThat(formData).containsEntry("uploadDuration", "1000");
        assertThat(formData).containsEntry("fileSize", String.valueOf(FILE_SIZE));

        Map<String, String> headers = httpEntityCaptor.getValue().getHeaders();
        assertThat(headers).containsEntry(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString());
    }

    @Test
    @DisplayName("initiateUpload should handle error response")
    void initiateUpload_shouldHandleErrorResponse() {
        // Arrange
        InitiateBinaryUploadOptions options = InitiateBinaryUploadOptions.builder()
                .damAssetFolder(DAM_ASSET_FOLDER)
                .fileName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .build();

        when(initiateUploadResponse.isSuccess()).thenReturn(false);
        when(initiateUploadResponse.getErrorMessage()).thenReturn("Error message");

        // Act
        AssetApiResponse<InitiateUploadResponse> response = directBinaryUploadApi.initiateUpload(options);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).isEqualTo("Error message");
    }

    @Test
    @DisplayName("uploadBinary should handle error when upload part fails")
    void uploadBinary_shouldHandleErrorWhenUploadPartFails() throws IOException {
        // Arrange
        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(binaryFile)
                .contentType(CONTENT_TYPE_VALUE)
                .uploadURIs(uploadURIs)
                .maxPartSize(512L)
                .build();

        Path part1 = tempDir.resolve("part1");
        Path part2 = tempDir.resolve("part2");
        Files.write(part1, "part1 data".getBytes());
        Files.write(part2, "part2 data".getBytes());
        List<Path> parts = Arrays.asList(part1, part2);

        when(uploadPartResponse.isSuccess()).thenReturn(false);
        when(uploadPartResponse.getErrorMessage()).thenReturn("Upload failed");

        try (MockedStatic<FileSplitUtil> fileSplitUtilMock = mockStatic(FileSplitUtil.class);
             MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            fileSplitUtilMock.when(() -> FileSplitUtil.splitFile(eq(binaryFile), anyLong()))
                    .thenReturn(parts);

            // Mock Files.newInputStream
            InputStream mockInputStream1 = Files.newInputStream(part1);
            filesMock.when(() -> Files.newInputStream(part1)).thenReturn(mockInputStream1);

            // Mock Files.delete
            filesMock.when(() -> Files.delete(any(Path.class))).thenReturn(true);

            // Act
            AssetApiResponse<UploadBinaryResponse> response = directBinaryUploadApi.uploadBinary(options);

            // Assert
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Failed to upload binary");

            // Verify delete was called for the first part
            filesMock.verify(() -> Files.delete(part1));
        }
    }

    @Test
    @DisplayName("completeUpload should handle error response")
    void completeUpload_shouldHandleErrorResponse() {
        // Arrange
        CompleteBinaryUploadOptions options = CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(CONTENT_TYPE_VALUE)
                .uploadToken(UPLOAD_TOKEN)
                .build();

        when(completeUploadResponse.isSuccess()).thenReturn(false);
        when(completeUploadResponse.getErrorMessage()).thenReturn("Error message");

        // Act
        AssetApiResponse<CompleteUploadResponse> response = directBinaryUploadApi.completeUpload(options);

        // Assert
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).isEqualTo("Error message");
    }
}
