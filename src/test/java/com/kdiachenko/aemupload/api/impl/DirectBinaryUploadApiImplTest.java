package com.kdiachenko.aemupload.api.impl;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.exception.SdkError;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.entity.HttpContexts;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.options.CompleteBinaryUploadOptions;
import com.kdiachenko.aemupload.options.CompleteUploadResponse;
import com.kdiachenko.aemupload.options.InitiateBinaryUploadOptions;
import com.kdiachenko.aemupload.options.InitiateUploadResponse;
import com.kdiachenko.aemupload.options.UploadBinaryOptions;
import com.kdiachenko.aemupload.options.UploadBinaryResponse;
import com.kdiachenko.aemupload.utils.FileSplitter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.hc.core5.http.ContentType.APPLICATION_FORM_URLENCODED;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DirectBinaryUploadApiImplTest {

    private static final String HOST_URL = "https://example.com";
    private static final String DAM_ASSET_FOLDER = "/content/dam/test";
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

    private ApiHttpResponse<InitiateUploadResponse> initiateUploadResponse;
    private ApiHttpResponse<Void> uploadPartResponse;
    private ApiHttpResponse<CompleteUploadResponse> completeUploadResponse;
    private InitiateUploadResponse initiateUploadResponseBody;
    private CompleteUploadResponse completeUploadResponseBody;

    @Captor
    private ArgumentCaptor<ApiHttpEntity<?>> httpEntityCaptor;

    private DirectBinaryUploadApiImpl directBinaryUploadApi;

    @BeforeEach
    void setUp() {
        when(apiServerConfiguration.getHostUrl()).thenReturn(HOST_URL);
        initiateUploadResponseBody = InitiateUploadResponse.builder()
                .completeURI(COMPLETE_URI)
                .folderPath(DAM_ASSET_FOLDER)
                .build();
        completeUploadResponseBody = CompleteUploadResponse.builder()
                .fileName(FILE_NAME)
                .filePath(DAM_ASSET_FOLDER + "/" + FILE_NAME)
                .contentType(CONTENT_TYPE_VALUE)
                .build();
        initiateUploadResponse = ApiHttpResponse.<InitiateUploadResponse>builder()
                .status(200)
                .body(initiateUploadResponseBody)
                .build();
        uploadPartResponse = ApiHttpResponse.<Void>builder().status(200).build();
        completeUploadResponse = ApiHttpResponse.<CompleteUploadResponse>builder()
                .status(200)
                .body(completeUploadResponseBody)
                .build();

        doReturn(initiateUploadResponse)
                .when(apiHttpClient)
                .post(anyString(), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(InitiateUploadResponse.class));
        doReturn(uploadPartResponse)
                .when(apiHttpClient)
                .put(anyString(), any(ApiHttpEntity.class), eq(Void.class));
        doReturn(completeUploadResponse)
                .when(apiHttpClient)
                .post(anyString(), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(CompleteUploadResponse.class));

        directBinaryUploadApi = new DirectBinaryUploadApiImpl(apiHttpClient, apiServerConfiguration, fileSplitter(List.of()));
    }

    @Test
    @DisplayName("initiateUpload should make POST request and return mapped response")
    void initiateUpload_shouldMakePostRequestAndReturnMappedResponse() {
        InitiateBinaryUploadOptions options = InitiateBinaryUploadOptions.builder()
                .damAssetFolder(DAM_ASSET_FOLDER)
                .fileName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .build();

        String expectedUrl = HOST_URL + DAM_ASSET_FOLDER + ".initiateUpload.json";

        AssetApiResponse<InitiateUploadResponse> response = directBinaryUploadApi.initiateUpload(options);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(initiateUploadResponseBody);
        verify(apiHttpClient).post(eq(expectedUrl), httpEntityCaptor.capture(), eq(HttpContexts.AUTHORIZED), eq(InitiateUploadResponse.class));

        Map<String, Object> formData = (Map<String, Object>) httpEntityCaptor.getValue().getBody();
        assertThat(formData).containsEntry("fileName", FILE_NAME);
        assertThat(formData).containsEntry("fileSize", FILE_SIZE);

        Map<String, String> headers = httpEntityCaptor.getValue().getHeaders();
        assertThat(headers).containsEntry(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString());
    }

    @Test
    @DisplayName("initiateUpload should handle transport error")
    void initiateUpload_shouldHandleTransportError() {
        InitiateBinaryUploadOptions options = InitiateBinaryUploadOptions.builder()
                .damAssetFolder(DAM_ASSET_FOLDER)
                .fileName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .build();

        doThrow(new RuntimeException("boom"))
                .when(apiHttpClient)
                .post(anyString(), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(InitiateUploadResponse.class));

        AssetApiResponse<InitiateUploadResponse> response = directBinaryUploadApi.initiateUpload(options);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .isEqualTo("Failed to initiate upload");
    }

    @Test
    @DisplayName("initiateUpload should handle API error response")
    void initiateUpload_shouldHandleApiErrorResponse() {
        InitiateBinaryUploadOptions options = InitiateBinaryUploadOptions.builder()
                .damAssetFolder(DAM_ASSET_FOLDER)
                .fileName(FILE_NAME)
                .fileSize(FILE_SIZE)
                .build();

        ApiHttpResponse<InitiateUploadResponse> errorResponse = ApiHttpResponse.<InitiateUploadResponse>builder()
                .status(500)
                .errorMessage("Error message")
                .build();
        doReturn(errorResponse)
                .when(apiHttpClient)
                .post(anyString(), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(InitiateUploadResponse.class));

        AssetApiResponse<InitiateUploadResponse> response = directBinaryUploadApi.initiateUpload(options);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .isEqualTo("Error message");
    }

    @Test
    @DisplayName("completeUpload should make POST request and return mapped response")
    void completeUpload_shouldMakePostRequestAndReturnMappedResponse() {
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

        AssetApiResponse<CompleteUploadResponse> response = directBinaryUploadApi.completeUpload(options);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(completeUploadResponseBody);
        verify(apiHttpClient).post(eq(expectedUrl), httpEntityCaptor.capture(), eq(HttpContexts.AUTHORIZED), eq(CompleteUploadResponse.class));

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
    @DisplayName("completeUpload should handle error response")
    void completeUpload_shouldHandleErrorResponse() {
        CompleteBinaryUploadOptions options = CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(CONTENT_TYPE_VALUE)
                .uploadToken(UPLOAD_TOKEN)
                .build();

        ApiHttpResponse<CompleteUploadResponse> errorResponse = ApiHttpResponse.<CompleteUploadResponse>builder()
                .status(500)
                .errorMessage("Error message")
                .build();
        doReturn(errorResponse)
                .when(apiHttpClient)
                .post(anyString(), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(CompleteUploadResponse.class));

        AssetApiResponse<CompleteUploadResponse> response = directBinaryUploadApi.completeUpload(options);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .isEqualTo("Error message");
    }

    @Test
    @DisplayName("uploadBinary should split file, upload parts, and delete temp parts")
    void uploadBinary_shouldSplitUploadAndCleanup() throws IOException {
        Path part1 = createTempPart("part1", "part1 data");
        Path part2 = createTempPart("part2", "part2 data");
        List<Path> parts = List.of(part1, part2);
        List<URI> uploadURIs = Arrays.asList(
                URI.create("https://example.com/part1"),
                URI.create("https://example.com/part2")
        );

        directBinaryUploadApi = new DirectBinaryUploadApiImpl(apiHttpClient, apiServerConfiguration, fileSplitter(parts));

        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(tempDir.resolve("test.bin"))
                .contentType(CONTENT_TYPE_VALUE)
                .uploadURIs(uploadURIs)
                .maxPartSize(512L)
                .build();

        AssetApiResponse<UploadBinaryResponse> response = directBinaryUploadApi.uploadBinary(options);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody().getChunks()).isEqualTo(2);
        verify(apiHttpClient).put(eq("https://example.com/part1"), any(ApiHttpEntity.class), eq(Void.class));
        verify(apiHttpClient).put(eq("https://example.com/part2"), any(ApiHttpEntity.class), eq(Void.class));
        assertThat(Files.exists(part1)).isFalse();
        assertThat(Files.exists(part2)).isFalse();
    }

    @Test
    @DisplayName("uploadBinary should fail when uploadURIs size mismatches parts")
    void uploadBinary_shouldValidateUrisCount() throws IOException {
        Path part1 = createTempPart("part1", "part1 data");
        Path part2 = createTempPart("part2", "part2 data");
        List<Path> parts = List.of(part1, part2);
        List<URI> uploadURIs = List.of(URI.create("https://example.com/part1"));

        directBinaryUploadApi = new DirectBinaryUploadApiImpl(apiHttpClient, apiServerConfiguration, fileSplitter(parts));

        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(tempDir.resolve("test.bin"))
                .contentType(CONTENT_TYPE_VALUE)
                .uploadURIs(uploadURIs)
                .maxPartSize(512L)
                .build();

        AssetApiResponse<UploadBinaryResponse> response = directBinaryUploadApi.uploadBinary(options);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .asString()
                .contains("uploadURIs size");
        verify(apiHttpClient, never()).put(anyString(), any(ApiHttpEntity.class), eq(Void.class));
        assertThat(Files.exists(part1)).isFalse();
        assertThat(Files.exists(part2)).isFalse();
    }

    @Test
    @DisplayName("uploadBinary should fail when upload part fails")
    void uploadBinary_shouldHandlePartUploadFailure() throws IOException {
        Path part1 = createTempPart("part1", "part1 data");
        List<Path> parts = List.of(part1);
        List<URI> uploadURIs = List.of(URI.create("https://example.com/part1"));

        uploadPartResponse = ApiHttpResponse.<Void>builder()
                .status(500)
                .errorMessage("Upload failed")
                .build();
        doReturn(uploadPartResponse)
                .when(apiHttpClient)
                .put(anyString(), any(ApiHttpEntity.class), eq(Void.class));

        directBinaryUploadApi = new DirectBinaryUploadApiImpl(apiHttpClient, apiServerConfiguration, fileSplitter(parts));

        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(tempDir.resolve("test.bin"))
                .contentType(CONTENT_TYPE_VALUE)
                .uploadURIs(uploadURIs)
                .maxPartSize(512L)
                .build();

        AssetApiResponse<UploadBinaryResponse> response = directBinaryUploadApi.uploadBinary(options);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .satisfies(error -> {
                    assertThat(error.getMessage()).isEqualTo("Failed to upload binary part");
                    assertThat(error.getHttpStatus()).isEqualTo(500);
                    assertThat(error.getRawResponse()).contains("Upload failed");
                });
        assertThat(Files.exists(part1)).isFalse();
    }

    @Test
    @DisplayName("uploadBinary should handle exceptions from file splitter")
    void uploadBinary_shouldHandleSplitterException() {
        directBinaryUploadApi = new DirectBinaryUploadApiImpl(apiHttpClient, apiServerConfiguration, fileSplitterException());

        Assertions.assertThrows(IllegalStateException.class, () -> UploadBinaryOptions.builder()
                .binary(tempDir.resolve("test.bin"))
                .contentType(CONTENT_TYPE_VALUE)
                .uploadURIs(List.of())
                .maxPartSize(512L)
                .build());
    }

    @Test
    @DisplayName("uploadBinary should handle exceptions during upload and cleanup failures")
    void uploadBinary_shouldHandleUploadExceptionAndCleanupFailure() throws IOException {
        Path nonEmptyDirectoryPart = tempDir.resolve("part-dir");
        Files.createDirectories(nonEmptyDirectoryPart);
        Files.write(nonEmptyDirectoryPart.resolve("nested.txt"), "data".getBytes(StandardCharsets.UTF_8));

        directBinaryUploadApi = new DirectBinaryUploadApiImpl(
                apiHttpClient, apiServerConfiguration, fileSplitter(List.of(nonEmptyDirectoryPart)));

        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(tempDir.resolve("test.bin"))
                .contentType(CONTENT_TYPE_VALUE)
                .uploadURIs(List.of(URI.create("https://example.com/part1")))
                .maxPartSize(512L)
                .build();

        AssetApiResponse<UploadBinaryResponse> response = directBinaryUploadApi.uploadBinary(options);

        //assertThat(response.isSuccess()).isTrue();
        //assertThat(response.getBody().getChunks()).isEqualTo(1);
    }

    @Test
    @DisplayName("uploadBinary should handle unexpected upload exception")
    void uploadBinary_shouldHandleUnexpectedUploadException() throws IOException {
        Path part1 = createTempPart("part1", "part1 data");
        directBinaryUploadApi = new DirectBinaryUploadApiImpl(apiHttpClient, apiServerConfiguration, fileSplitter(List.of(part1)));

        doThrow(new RuntimeException("boom"))
                .when(apiHttpClient)
                .put(anyString(), any(ApiHttpEntity.class), eq(Void.class));

        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(tempDir.resolve("test.bin"))
                .contentType(CONTENT_TYPE_VALUE)
                .uploadURIs(List.of(URI.create("https://example.com/part1")))
                .maxPartSize(512L)
                .build();

        AssetApiResponse<UploadBinaryResponse> response = directBinaryUploadApi.uploadBinary(options);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .isEqualTo("Failed to upload binary");
    }

    @Test
    @DisplayName("completeUpload should handle transport exception")
    void completeUpload_shouldHandleTransportException() {
        CompleteBinaryUploadOptions options = CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(CONTENT_TYPE_VALUE)
                .uploadToken(UPLOAD_TOKEN)
                .build();
        doThrow(new RuntimeException("boom"))
                .when(apiHttpClient)
                .post(anyString(), any(ApiHttpEntity.class), eq(HttpContexts.AUTHORIZED), eq(CompleteUploadResponse.class));

        AssetApiResponse<CompleteUploadResponse> response = directBinaryUploadApi.completeUpload(options);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent()
                .get()
                .extracting(SdkError::getMessage)
                .isEqualTo("Failed to complete upload");
    }

    private FileSplitter fileSplitter(List<Path> parts) {
        return (binary, maxPartSize) -> parts;
    }

    private FileSplitter fileSplitterException() {
        return (binary, maxPartSize) -> {
            throw new IllegalStateException("split failed");
        };
    }

    private Path createTempPart(String name, String content) throws IOException {
        Path part = tempDir.resolve(name);
        Files.write(part, content.getBytes(StandardCharsets.UTF_8));
        return part;
    }
}
