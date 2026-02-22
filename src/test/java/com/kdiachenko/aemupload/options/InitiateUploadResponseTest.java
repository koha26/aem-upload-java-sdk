package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InitiateUploadResponseTest {

    @Test
    void defaultConstructor_shouldCreateEmptyObject() {
        // When
        InitiateUploadResponse response = new InitiateUploadResponse();

        // Then
        assertThat(response.getCompleteURI()).isNull();
        assertThat(response.getFolderPath()).isNull();
        assertThat(response.getFiles()).isNotNull();
        assertThat(response.getFiles()).isEmpty();
    }

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFields() {
        // Given
        String completeURI = "https://example.com/complete";
        String folderPath = "/content/dam/folder";
        List<UploadingAssetFile> files = new ArrayList<>();
        files.add(createUploadingAssetFile("test1.jpg"));
        files.add(createUploadingAssetFile("test2.jpg"));

        // When
        InitiateUploadResponse response = new InitiateUploadResponse(completeURI, folderPath, files);

        // Then
        assertThat(response.getCompleteURI()).isEqualTo(completeURI);
        assertThat(response.getFolderPath()).isEqualTo(folderPath);
        assertThat(response.getFiles()).isEqualTo(files);
    }

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        String completeURI = "https://example.com/complete";
        String folderPath = "/content/dam/folder";
        List<UploadingAssetFile> files = new ArrayList<>();
        files.add(createUploadingAssetFile("test1.jpg"));
        files.add(createUploadingAssetFile("test2.jpg"));

        // When
        InitiateUploadResponse response = InitiateUploadResponse.builder()
                .completeURI(completeURI)
                .folderPath(folderPath)
                .files(files)
                .build();

        // Then
        assertThat(response.getCompleteURI()).isEqualTo(completeURI);
        assertThat(response.getFolderPath()).isEqualTo(folderPath);
        assertThat(response.getFiles()).isEqualTo(files);
    }

    @Test
    void builder_shouldInitializeFilesWithEmptyList() {
        // When
        InitiateUploadResponse response = InitiateUploadResponse.builder().build();

        // Then
        assertThat(response.getFiles()).isNotNull();
        assertThat(response.getFiles()).isEmpty();
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Given
        InitiateUploadResponse response = new InitiateUploadResponse();
        String completeURI = "https://example.com/complete";
        String folderPath = "/content/dam/folder";
        List<UploadingAssetFile> files = new ArrayList<>();
        files.add(createUploadingAssetFile("test1.jpg"));
        files.add(createUploadingAssetFile("test2.jpg"));

        // When
        response.setCompleteURI(completeURI);
        response.setFolderPath(folderPath);
        response.setFiles(files);

        // Then
        assertThat(response.getCompleteURI()).isEqualTo(completeURI);
        assertThat(response.getFolderPath()).isEqualTo(folderPath);
        assertThat(response.getFiles()).isEqualTo(files);
    }

    @Test
    void files_shouldAllowAddingAndRetrievingValues() {
        // Given
        InitiateUploadResponse response = new InitiateUploadResponse();
        UploadingAssetFile file1 = createUploadingAssetFile("test1.jpg");
        UploadingAssetFile file2 = createUploadingAssetFile("test2.jpg");

        // When
        response.getFiles().add(file1);
        response.getFiles().add(file2);

        // Then
        assertThat(response.getFiles()).hasSize(2);
        assertThat(response.getFiles()).containsExactly(file1, file2);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        InitiateUploadResponse response1 = InitiateUploadResponse.builder()
                .completeURI("https://example.com/complete")
                .folderPath("/content/dam/folder")
                .build();
        response1.getFiles().add(createUploadingAssetFile("test.jpg"));

        InitiateUploadResponse response2 = InitiateUploadResponse.builder()
                .completeURI("https://example.com/complete")
                .folderPath("/content/dam/folder")
                .build();
        response2.getFiles().add(createUploadingAssetFile("test.jpg"));

        InitiateUploadResponse response3 = InitiateUploadResponse.builder()
                .completeURI("https://example.com/different")
                .folderPath("/content/dam/different")
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        assertThat(response1).isNotEqualTo(response3);
        assertThat(response1.hashCode()).isNotEqualTo(response3.hashCode());
    }

    @Test
    void toString_shouldIncludeAllFields() {
        // Given
        String completeURI = "https://example.com/complete";
        String folderPath = "/content/dam/folder";
        InitiateUploadResponse response = InitiateUploadResponse.builder()
                .completeURI(completeURI)
                .folderPath(folderPath)
                .build();
        response.getFiles().add(createUploadingAssetFile("test.jpg"));

        // When
        String toString = response.toString();

        // Then
        assertThat(toString).contains(completeURI);
        assertThat(toString).contains(folderPath);
        assertThat(toString).contains("files=");
    }

    private UploadingAssetFile createUploadingAssetFile(String fileName) {
        return UploadingAssetFile.builder()
                .fileName(fileName)
                .mimeType("image/jpeg")
                .uploadToken("token-" + fileName)
                .minPartSize(1024L)
                .maxPartSize(2048L)
                .build();
    }
}
