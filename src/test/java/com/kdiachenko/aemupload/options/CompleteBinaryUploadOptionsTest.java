package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompleteBinaryUploadOptionsTest {

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        String completeUri = "https://example.com/complete";
        String fileName = "test.jpg";
        String mimeType = "image/jpeg";
        String uploadToken = "token123";
        boolean createVersion = true;
        String versionLabel = "v1.0";
        String versionComment = "Initial version";
        boolean replace = false;
        long uploadDuration = 1000L;
        long fileSize = 1024L;

        // When
        CompleteBinaryUploadOptions options = CompleteBinaryUploadOptions.builder()
                .completeUri(completeUri)
                .fileName(fileName)
                .mimeType(mimeType)
                .uploadToken(uploadToken)
                .createVersion(createVersion)
                .versionLabel(versionLabel)
                .versionComment(versionComment)
                .replace(replace)
                .uploadDuration(uploadDuration)
                .fileSize(fileSize)
                .build();

        // Then
        assertThat(options.getCompleteUri()).isEqualTo(completeUri);
        assertThat(options.getFileName()).isEqualTo(fileName);
        assertThat(options.getMimeType()).isEqualTo(mimeType);
        assertThat(options.getUploadToken()).isEqualTo(uploadToken);
        assertThat(options.isCreateVersion()).isEqualTo(createVersion);
        assertThat(options.getVersionLabel()).isEqualTo(versionLabel);
        assertThat(options.getVersionComment()).isEqualTo(versionComment);
        assertThat(options.isReplace()).isEqualTo(replace);
        assertThat(options.getUploadDuration()).isEqualTo(uploadDuration);
        assertThat(options.getFileSize()).isEqualTo(fileSize);
    }

    @Test
    void builder_shouldCreateObjectWithDefaultValues() {
        // When
        CompleteBinaryUploadOptions options = CompleteBinaryUploadOptions.builder().build();

        // Then
        assertThat(options.getCompleteUri()).isNull();
        assertThat(options.getFileName()).isNull();
        assertThat(options.getMimeType()).isNull();
        assertThat(options.getUploadToken()).isNull();
        assertThat(options.isCreateVersion()).isFalse();
        assertThat(options.getVersionLabel()).isNull();
        assertThat(options.getVersionComment()).isNull();
        assertThat(options.isReplace()).isFalse();
        assertThat(options.getUploadDuration()).isEqualTo(0L);
        assertThat(options.getFileSize()).isEqualTo(0L);
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        // Given
        CompleteBinaryUploadOptions options = CompleteBinaryUploadOptions.builder().build();
        String completeUri = "https://example.com/complete";
        String fileName = "test.jpg";
        String mimeType = "image/jpeg";
        String uploadToken = "token123";
        boolean createVersion = true;
        String versionLabel = "v1.0";
        String versionComment = "Initial version";
        boolean replace = false;
        long uploadDuration = 1000L;
        long fileSize = 1024L;

        // When
        options.setCompleteUri(completeUri);
        options.setFileName(fileName);
        options.setMimeType(mimeType);
        options.setUploadToken(uploadToken);
        options.setCreateVersion(createVersion);
        options.setVersionLabel(versionLabel);
        options.setVersionComment(versionComment);
        options.setReplace(replace);
        options.setUploadDuration(uploadDuration);
        options.setFileSize(fileSize);

        // Then
        assertThat(options.getCompleteUri()).isEqualTo(completeUri);
        assertThat(options.getFileName()).isEqualTo(fileName);
        assertThat(options.getMimeType()).isEqualTo(mimeType);
        assertThat(options.getUploadToken()).isEqualTo(uploadToken);
        assertThat(options.isCreateVersion()).isEqualTo(createVersion);
        assertThat(options.getVersionLabel()).isEqualTo(versionLabel);
        assertThat(options.getVersionComment()).isEqualTo(versionComment);
        assertThat(options.isReplace()).isEqualTo(replace);
        assertThat(options.getUploadDuration()).isEqualTo(uploadDuration);
        assertThat(options.getFileSize()).isEqualTo(fileSize);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        CompleteBinaryUploadOptions options1 = CompleteBinaryUploadOptions.builder()
                .completeUri("https://example.com/complete")
                .fileName("test.jpg")
                .uploadToken("token123")
                .build();

        CompleteBinaryUploadOptions options2 = CompleteBinaryUploadOptions.builder()
                .completeUri("https://example.com/complete")
                .fileName("test.jpg")
                .uploadToken("token123")
                .build();

        CompleteBinaryUploadOptions options3 = CompleteBinaryUploadOptions.builder()
                .completeUri("https://example.com/different")
                .fileName("different.jpg")
                .uploadToken("different")
                .build();

        // Then
        assertThat(options1).isEqualTo(options2);
        assertThat(options1.hashCode()).isEqualTo(options2.hashCode());
        assertThat(options1).isNotEqualTo(options3);
        assertThat(options1.hashCode()).isNotEqualTo(options3.hashCode());
    }
}
