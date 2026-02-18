package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadBinaryOptionsTest {

    @TempDir
    Path tempDir;

    private static final List<URI> UPLOAD_URIS = Arrays.asList(
            URI.create("https://example.com/upload/1"),
            URI.create("https://example.com/upload/2")
    );

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        Path binary = tempDir.resolve("test.jpg");
        long minPartSize = 1024L;
        long maxPartSize = 2048L;
        String contentType = "image/jpeg";

        // When
        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(UPLOAD_URIS)
                .minPartSize(minPartSize)
                .maxPartSize(maxPartSize)
                .contentType(contentType)
                .build();

        // Then
        assertThat(options.getBinary()).isEqualTo(binary);
        assertThat(options.getUploadURIs()).isEqualTo(UPLOAD_URIS);
        assertThat(options.getMinPartSize()).isEqualTo(minPartSize);
        assertThat(options.getMaxPartSize()).isEqualTo(maxPartSize);
        assertThat(options.getContentType()).isEqualTo(contentType);
    }

    @Test
    void builder_shouldValidateRequiredFields() {
        Path binary = tempDir.resolve("test.jpg");

        // binary is required
        assertThatThrownBy(() -> UploadBinaryOptions.builder()
                .uploadURIs(UPLOAD_URIS)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("binary");

        // uploadURIs is required
        assertThatThrownBy(() -> UploadBinaryOptions.builder()
                .binary(binary)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("uploadURIs");

        // maxPartSize must be > 0
        assertThatThrownBy(() -> UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(UPLOAD_URIS)
                .maxPartSize(0L)
                .contentType("image/jpeg")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("maxPartSize");

        // contentType is required
        assertThatThrownBy(() -> UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(UPLOAD_URIS)
                .maxPartSize(2048L)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("contentType");
    }

    @Test
    void builder_shouldRejectBlankContentType() {
        Path binary = tempDir.resolve("test.jpg");
        assertThatThrownBy(() -> UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(UPLOAD_URIS)
                .maxPartSize(2048L)
                .contentType(" ")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("contentType");
    }

    @Test
    void builder_shouldHandleNullUploadUrisInputAndNullInternalList() throws Exception {
        Path binary = tempDir.resolve("test.jpg");

        assertThatThrownBy(() -> UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(null)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("uploadURIs");

        UploadBinaryOptions.UploadBinaryOptionsBuilder builder = UploadBinaryOptions.builder()
                .binary(binary)
                .maxPartSize(2048L)
                .contentType("image/jpeg");
        Field uploadUrisField = builder.getClass().getDeclaredField("uploadURIs");
        uploadUrisField.setAccessible(true);
        uploadUrisField.set(builder, null);

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("uploadURIs");
    }

    @Test
    void uploadURIs_shouldBeImmutable() {
        // Given
        Path binary = tempDir.resolve("test.jpg");
        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(UPLOAD_URIS)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build();

        // Then - list should be unmodifiable
        assertThatThrownBy(() -> options.getUploadURIs().add(URI.create("https://example.com/new")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void toBuilder_shouldAllowModification() {
        // Given
        Path binary = tempDir.resolve("test.jpg");
        UploadBinaryOptions original = UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(UPLOAD_URIS)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build();

        // When
        List<URI> newUris = List.of(URI.create("https://new.com/upload"));
        UploadBinaryOptions modified = original.toBuilder()
                .uploadURIs(newUris)
                .build();

        // Then
        assertThat(modified.getBinary()).isEqualTo(binary);
        assertThat(modified.getUploadURIs()).isEqualTo(newUris);
        assertThat(modified.getMaxPartSize()).isEqualTo(2048L);
        assertThat(modified.getContentType()).isEqualTo("image/jpeg");
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        Path binary1 = Paths.get("/path/to/test.jpg");
        UploadBinaryOptions options1 = UploadBinaryOptions.builder()
                .binary(binary1)
                .uploadURIs(UPLOAD_URIS)
                .minPartSize(1024L)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build();

        Path binary2 = Paths.get("/path/to/test.jpg");
        UploadBinaryOptions options2 = UploadBinaryOptions.builder()
                .binary(binary2)
                .uploadURIs(UPLOAD_URIS)
                .minPartSize(1024L)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build();

        Path binary3 = Paths.get("/path/to/different.jpg");
        UploadBinaryOptions options3 = UploadBinaryOptions.builder()
                .binary(binary3)
                .uploadURIs(UPLOAD_URIS)
                .minPartSize(512L)
                .maxPartSize(1024L)
                .contentType("image/png")
                .build();

        // Then
        assertThat(options1).isEqualTo(options2);
        assertThat(options1.hashCode()).isEqualTo(options2.hashCode());
        assertThat(options1).isNotEqualTo(options3);
        assertThat(options1.hashCode()).isNotEqualTo(options3.hashCode());
    }

    @Test
    void toString_shouldIncludeAllFields() {
        // Given
        Path binary = Paths.get("/path/to/test.jpg");
        long minPartSize = 1024L;
        long maxPartSize = 2048L;
        String contentType = "image/jpeg";
        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(UPLOAD_URIS)
                .minPartSize(minPartSize)
                .maxPartSize(maxPartSize)
                .contentType(contentType)
                .build();

        // When
        String toString = options.toString();

        // Then
        assertThat(toString).contains(binary.toString());
        assertThat(toString).contains(String.valueOf(minPartSize));
        assertThat(toString).contains(String.valueOf(maxPartSize));
        assertThat(toString).contains(contentType);
        assertThat(toString).contains("uploadURIs=");
    }
}
