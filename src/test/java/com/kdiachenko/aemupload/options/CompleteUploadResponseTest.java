package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompleteUploadResponseTest {

    @Test
    void defaultConstructor_shouldCreateEmptyObject() {
        // When
        CompleteUploadResponse response = new CompleteUploadResponse();

        // Then
        assertThat(response.getFileName()).isNull();
        assertThat(response.getFilePath()).isNull();
        assertThat(response.getContentType()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFields() {
        // Given
        String fileName = "test.jpg";
        String filePath = "/content/dam/test.jpg";
        String contentType = "image/jpeg";

        // When
        CompleteUploadResponse response = new CompleteUploadResponse(fileName, filePath, contentType);

        // Then
        assertThat(response.getFileName()).isEqualTo(fileName);
        assertThat(response.getFilePath()).isEqualTo(filePath);
        assertThat(response.getContentType()).isEqualTo(contentType);
    }

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        String fileName = "test.jpg";
        String filePath = "/content/dam/test.jpg";
        String contentType = "image/jpeg";

        // When
        CompleteUploadResponse response = CompleteUploadResponse.builder()
                .fileName(fileName)
                .filePath(filePath)
                .contentType(contentType)
                .build();

        // Then
        assertThat(response.getFileName()).isEqualTo(fileName);
        assertThat(response.getFilePath()).isEqualTo(filePath);
        assertThat(response.getContentType()).isEqualTo(contentType);
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Given
        CompleteUploadResponse response = new CompleteUploadResponse();
        String fileName = "test.jpg";
        String filePath = "/content/dam/test.jpg";
        String contentType = "image/jpeg";

        // When
        response.setFileName(fileName);
        response.setFilePath(filePath);
        response.setContentType(contentType);

        // Then
        assertThat(response.getFileName()).isEqualTo(fileName);
        assertThat(response.getFilePath()).isEqualTo(filePath);
        assertThat(response.getContentType()).isEqualTo(contentType);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        CompleteUploadResponse response1 = CompleteUploadResponse.builder()
                .fileName("test.jpg")
                .filePath("/content/dam/test.jpg")
                .contentType("image/jpeg")
                .build();

        CompleteUploadResponse response2 = CompleteUploadResponse.builder()
                .fileName("test.jpg")
                .filePath("/content/dam/test.jpg")
                .contentType("image/jpeg")
                .build();

        CompleteUploadResponse response3 = CompleteUploadResponse.builder()
                .fileName("different.jpg")
                .filePath("/content/dam/different.jpg")
                .contentType("image/png")
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        assertThat(response1).isNotEqualTo(response3);
        assertThat(response1.hashCode()).isNotEqualTo(response3.hashCode());
    }
}
