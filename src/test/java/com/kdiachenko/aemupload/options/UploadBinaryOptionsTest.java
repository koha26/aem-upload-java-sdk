package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UploadBinaryOptionsTest {

    @TempDir
    Path tempDir;

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        Path binary = tempDir.resolve("test.jpg");
        List<URI> uploadURIs = Arrays.asList(
                URI.create("https://example.com/upload/1"),
                URI.create("https://example.com/upload/2")
        );
        long minPartSize = 1024L;
        long maxPartSize = 2048L;
        String contentType = "image/jpeg";

        // When
        UploadBinaryOptions options = UploadBinaryOptions.builder()
                .binary(binary)
                .uploadURIs(uploadURIs)
                .minPartSize(minPartSize)
                .maxPartSize(maxPartSize)
                .contentType(contentType)
                .build();

        // Then
        assertThat(options.getBinary()).isEqualTo(binary);
        assertThat(options.getUploadURIs()).isEqualTo(uploadURIs);
        assertThat(options.getMinPartSize()).isEqualTo(minPartSize);
        assertThat(options.getMaxPartSize()).isEqualTo(maxPartSize);
        assertThat(options.getContentType()).isEqualTo(contentType);
    }

    @Test
    void builder_shouldInitializeUploadURIsWithEmptyList() {
        // When
        UploadBinaryOptions options = UploadBinaryOptions.builder().build();

        // Then
        assertThat(options.getUploadURIs()).isNotNull();
        assertThat(options.getUploadURIs()).isEmpty();
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        // Given
        UploadBinaryOptions options = UploadBinaryOptions.builder().build();
        Path binary = tempDir.resolve("test.jpg");
        List<URI> uploadURIs = new ArrayList<>();
        uploadURIs.add(URI.create("https://example.com/upload/1"));
        uploadURIs.add(URI.create("https://example.com/upload/2"));
        long minPartSize = 1024L;
        long maxPartSize = 2048L;
        String contentType = "image/jpeg";

        // When
        options.setBinary(binary);
        options.setUploadURIs(uploadURIs);
        options.setMinPartSize(minPartSize);
        options.setMaxPartSize(maxPartSize);
        options.setContentType(contentType);

        // Then
        assertThat(options.getBinary()).isEqualTo(binary);
        assertThat(options.getUploadURIs()).isEqualTo(uploadURIs);
        assertThat(options.getMinPartSize()).isEqualTo(minPartSize);
        assertThat(options.getMaxPartSize()).isEqualTo(maxPartSize);
        assertThat(options.getContentType()).isEqualTo(contentType);
    }

    @Test
    void uploadURIs_shouldAllowAddingAndRetrievingValues() {
        // Given
        UploadBinaryOptions options = UploadBinaryOptions.builder().build();
        URI uri1 = URI.create("https://example.com/upload/1");
        URI uri2 = URI.create("https://example.com/upload/2");

        // When
        options.getUploadURIs().add(uri1);
        options.getUploadURIs().add(uri2);

        // Then
        assertThat(options.getUploadURIs()).hasSize(2);
        assertThat(options.getUploadURIs()).containsExactly(uri1, uri2);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        Path binary1 = Paths.get("/path/to/test.jpg");
        UploadBinaryOptions options1 = UploadBinaryOptions.builder()
                .binary(binary1)
                .minPartSize(1024L)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build();
        options1.getUploadURIs().add(URI.create("https://example.com/upload/1"));

        Path binary2 = Paths.get("/path/to/test.jpg");
        UploadBinaryOptions options2 = UploadBinaryOptions.builder()
                .binary(binary2)
                .minPartSize(1024L)
                .maxPartSize(2048L)
                .contentType("image/jpeg")
                .build();
        options2.getUploadURIs().add(URI.create("https://example.com/upload/1"));

        Path binary3 = Paths.get("/path/to/different.jpg");
        UploadBinaryOptions options3 = UploadBinaryOptions.builder()
                .binary(binary3)
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
                .minPartSize(minPartSize)
                .maxPartSize(maxPartSize)
                .contentType(contentType)
                .build();
        options.getUploadURIs().add(URI.create("https://example.com/upload/1"));

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
