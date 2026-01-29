package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompleteBinaryUploadOptionsTest {

    private static final String COMPLETE_URI = "https://example.com/complete";
    private static final String FILE_NAME = "test.jpg";
    private static final String MIME_TYPE = "image/jpeg";
    private static final String UPLOAD_TOKEN = "token123";

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        String versionLabel = "v1.0";
        String versionComment = "Initial version";
        boolean createVersion = true;
        boolean replace = false;
        long uploadDuration = 1000L;
        long fileSize = 1024L;

        // When
        CompleteBinaryUploadOptions options = CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(MIME_TYPE)
                .uploadToken(UPLOAD_TOKEN)
                .createVersion(createVersion)
                .versionLabel(versionLabel)
                .versionComment(versionComment)
                .replace(replace)
                .uploadDuration(uploadDuration)
                .fileSize(fileSize)
                .build();

        // Then
        assertThat(options.getCompleteUri()).isEqualTo(COMPLETE_URI);
        assertThat(options.getFileName()).isEqualTo(FILE_NAME);
        assertThat(options.getMimeType()).isEqualTo(MIME_TYPE);
        assertThat(options.getUploadToken()).isEqualTo(UPLOAD_TOKEN);
        assertThat(options.isCreateVersion()).isEqualTo(createVersion);
        assertThat(options.getVersionLabel()).isEqualTo(versionLabel);
        assertThat(options.getVersionComment()).isEqualTo(versionComment);
        assertThat(options.isReplace()).isEqualTo(replace);
        assertThat(options.getUploadDuration()).isEqualTo(uploadDuration);
        assertThat(options.getFileSize()).isEqualTo(fileSize);
    }

    @Test
    void builder_shouldValidateRequiredFields() {
        // completeUri is required
        assertThatThrownBy(() -> CompleteBinaryUploadOptions.builder()
                .fileName(FILE_NAME)
                .mimeType(MIME_TYPE)
                .uploadToken(UPLOAD_TOKEN)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("completeUri");

        // fileName is required
        assertThatThrownBy(() -> CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .mimeType(MIME_TYPE)
                .uploadToken(UPLOAD_TOKEN)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fileName");

        // mimeType is required
        assertThatThrownBy(() -> CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .uploadToken(UPLOAD_TOKEN)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("mimeType");

        // uploadToken is required
        assertThatThrownBy(() -> CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(MIME_TYPE)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("uploadToken");
    }

    @Test
    void toBuilder_shouldAllowModification() {
        // Given
        CompleteBinaryUploadOptions original = CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(MIME_TYPE)
                .uploadToken(UPLOAD_TOKEN)
                .build();

        // When
        CompleteBinaryUploadOptions modified = original.toBuilder()
                .fileName("modified.jpg")
                .build();

        // Then
        assertThat(modified.getCompleteUri()).isEqualTo(COMPLETE_URI);
        assertThat(modified.getFileName()).isEqualTo("modified.jpg");
        assertThat(modified.getMimeType()).isEqualTo(MIME_TYPE);
        assertThat(modified.getUploadToken()).isEqualTo(UPLOAD_TOKEN);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        CompleteBinaryUploadOptions options1 = CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(MIME_TYPE)
                .uploadToken(UPLOAD_TOKEN)
                .build();

        CompleteBinaryUploadOptions options2 = CompleteBinaryUploadOptions.builder()
                .completeUri(COMPLETE_URI)
                .fileName(FILE_NAME)
                .mimeType(MIME_TYPE)
                .uploadToken(UPLOAD_TOKEN)
                .build();

        CompleteBinaryUploadOptions options3 = CompleteBinaryUploadOptions.builder()
                .completeUri("https://example.com/different")
                .fileName("different.jpg")
                .mimeType("image/png")
                .uploadToken("different")
                .build();

        // Then
        assertThat(options1).isEqualTo(options2);
        assertThat(options1.hashCode()).isEqualTo(options2.hashCode());
        assertThat(options1).isNotEqualTo(options3);
        assertThat(options1.hashCode()).isNotEqualTo(options3.hashCode());
    }
}
