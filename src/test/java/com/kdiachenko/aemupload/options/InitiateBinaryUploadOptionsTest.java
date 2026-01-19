package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InitiateBinaryUploadOptionsTest {

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        String damAssetFolder = "/content/dam/folder";
        String fileName = "test.jpg";
        long fileSize = 1024L;

        // When
        InitiateBinaryUploadOptions options = InitiateBinaryUploadOptions.builder()
                .damAssetFolder(damAssetFolder)
                .fileName(fileName)
                .fileSize(fileSize)
                .build();

        // Then
        assertThat(options.getDamAssetFolder()).isEqualTo(damAssetFolder);
        assertThat(options.getFileName()).isEqualTo(fileName);
        assertThat(options.getFileSize()).isEqualTo(fileSize);
    }

    @Test
    void builder_shouldCreateObjectWithDefaultValues() {
        // When
        InitiateBinaryUploadOptions options = InitiateBinaryUploadOptions.builder().build();

        // Then
        assertThat(options.getDamAssetFolder()).isNull();
        assertThat(options.getFileName()).isNull();
        assertThat(options.getFileSize()).isEqualTo(0L);
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        // Given
        InitiateBinaryUploadOptions options = InitiateBinaryUploadOptions.builder().build();
        String damAssetFolder = "/content/dam/folder";
        String fileName = "test.jpg";
        long fileSize = 1024L;

        // When
        options.setDamAssetFolder(damAssetFolder);
        options.setFileName(fileName);
        options.setFileSize(fileSize);

        // Then
        assertThat(options.getDamAssetFolder()).isEqualTo(damAssetFolder);
        assertThat(options.getFileName()).isEqualTo(fileName);
        assertThat(options.getFileSize()).isEqualTo(fileSize);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        InitiateBinaryUploadOptions options1 = InitiateBinaryUploadOptions.builder()
                .damAssetFolder("/content/dam/folder")
                .fileName("test.jpg")
                .fileSize(1024L)
                .build();

        InitiateBinaryUploadOptions options2 = InitiateBinaryUploadOptions.builder()
                .damAssetFolder("/content/dam/folder")
                .fileName("test.jpg")
                .fileSize(1024L)
                .build();

        InitiateBinaryUploadOptions options3 = InitiateBinaryUploadOptions.builder()
                .damAssetFolder("/content/dam/different")
                .fileName("different.jpg")
                .fileSize(2048L)
                .build();

        // Then
        assertThat(options1).isEqualTo(options2);
        assertThat(options1.hashCode()).isEqualTo(options2.hashCode());
        assertThat(options1).isNotEqualTo(options3);
        assertThat(options1.hashCode()).isNotEqualTo(options3.hashCode());
    }
}
