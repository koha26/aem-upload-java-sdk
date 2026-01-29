package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void builder_shouldValidateRequiredFields() {
        // damAssetFolder is required
        assertThatThrownBy(() -> InitiateBinaryUploadOptions.builder()
                .fileName("test.jpg")
                .fileSize(1024L)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("damAssetFolder");

        // fileName is required
        assertThatThrownBy(() -> InitiateBinaryUploadOptions.builder()
                .damAssetFolder("/content/dam")
                .fileSize(1024L)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fileName");

        // fileSize must be > 0
        assertThatThrownBy(() -> InitiateBinaryUploadOptions.builder()
                .damAssetFolder("/content/dam")
                .fileName("test.jpg")
                .fileSize(0L)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fileSize");
    }

    @Test
    void toBuilder_shouldAllowModification() {
        // Given
        InitiateBinaryUploadOptions original = InitiateBinaryUploadOptions.builder()
                .damAssetFolder("/content/dam/folder")
                .fileName("test.jpg")
                .fileSize(1024L)
                .build();

        // When
        InitiateBinaryUploadOptions modified = original.toBuilder()
                .fileName("modified.jpg")
                .build();

        // Then
        assertThat(modified.getDamAssetFolder()).isEqualTo("/content/dam/folder");
        assertThat(modified.getFileName()).isEqualTo("modified.jpg");
        assertThat(modified.getFileSize()).isEqualTo(1024L);
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
