package com.kdiachenko.aemupload.options;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UploadingAssetFileTest {

    @Test
    void defaultConstructor_shouldCreateEmptyObject() {
        // When
        UploadingAssetFile file = new UploadingAssetFile();

        // Then
        assertThat(file.getFileName()).isNull();
        assertThat(file.getMimeType()).isNull();
        assertThat(file.getUploadToken()).isNull();
        assertThat(file.getUploadURIs()).isNotNull();
        assertThat(file.getUploadURIs()).isEmpty();
        assertThat(file.getMinPartSize()).isEqualTo(0L);
        assertThat(file.getMaxPartSize()).isEqualTo(0L);
    }

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFields() {
        // Given
        String fileName = "test.jpg";
        String mimeType = "image/jpeg";
        String uploadToken = "token123";
        List<URI> uploadURIs = Arrays.asList(
                URI.create("https://example.com/upload/1"),
                URI.create("https://example.com/upload/2")
        );
        long minPartSize = 1024L;
        long maxPartSize = 2048L;

        // When
        UploadingAssetFile file = new UploadingAssetFile(
                fileName, mimeType, uploadToken, uploadURIs, minPartSize, maxPartSize);

        // Then
        assertThat(file.getFileName()).isEqualTo(fileName);
        assertThat(file.getMimeType()).isEqualTo(mimeType);
        assertThat(file.getUploadToken()).isEqualTo(uploadToken);
        assertThat(file.getUploadURIs()).isEqualTo(uploadURIs);
        assertThat(file.getMinPartSize()).isEqualTo(minPartSize);
        assertThat(file.getMaxPartSize()).isEqualTo(maxPartSize);
    }

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        String fileName = "test.jpg";
        String mimeType = "image/jpeg";
        String uploadToken = "token123";
        List<URI> uploadURIs = Arrays.asList(
                URI.create("https://example.com/upload/1"),
                URI.create("https://example.com/upload/2")
        );
        long minPartSize = 1024L;
        long maxPartSize = 2048L;

        // When
        UploadingAssetFile file = UploadingAssetFile.builder()
                .fileName(fileName)
                .mimeType(mimeType)
                .uploadToken(uploadToken)
                .uploadURIs(uploadURIs)
                .minPartSize(minPartSize)
                .maxPartSize(maxPartSize)
                .build();

        // Then
        assertThat(file.getFileName()).isEqualTo(fileName);
        assertThat(file.getMimeType()).isEqualTo(mimeType);
        assertThat(file.getUploadToken()).isEqualTo(uploadToken);
        assertThat(file.getUploadURIs()).isEqualTo(uploadURIs);
        assertThat(file.getMinPartSize()).isEqualTo(minPartSize);
        assertThat(file.getMaxPartSize()).isEqualTo(maxPartSize);
    }

    @Test
    void builder_shouldInitializeUploadURIsWithEmptyList() {
        // When
        UploadingAssetFile file = UploadingAssetFile.builder().build();

        // Then
        assertThat(file.getUploadURIs()).isNotNull();
        assertThat(file.getUploadURIs()).isEmpty();
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Given
        UploadingAssetFile file = new UploadingAssetFile();
        String fileName = "test.jpg";
        String mimeType = "image/jpeg";
        String uploadToken = "token123";
        List<URI> uploadURIs = new ArrayList<>();
        uploadURIs.add(URI.create("https://example.com/upload/1"));
        uploadURIs.add(URI.create("https://example.com/upload/2"));
        long minPartSize = 1024L;
        long maxPartSize = 2048L;

        // When
        file.setFileName(fileName);
        file.setMimeType(mimeType);
        file.setUploadToken(uploadToken);
        file.setUploadURIs(uploadURIs);
        file.setMinPartSize(minPartSize);
        file.setMaxPartSize(maxPartSize);

        // Then
        assertThat(file.getFileName()).isEqualTo(fileName);
        assertThat(file.getMimeType()).isEqualTo(mimeType);
        assertThat(file.getUploadToken()).isEqualTo(uploadToken);
        assertThat(file.getUploadURIs()).isEqualTo(uploadURIs);
        assertThat(file.getMinPartSize()).isEqualTo(minPartSize);
        assertThat(file.getMaxPartSize()).isEqualTo(maxPartSize);
    }

    @Test
    void uploadURIs_shouldAllowAddingAndRetrievingValues() {
        // Given
        UploadingAssetFile file = new UploadingAssetFile();
        URI uri1 = URI.create("https://example.com/upload/1");
        URI uri2 = URI.create("https://example.com/upload/2");

        // When
        file.getUploadURIs().add(uri1);
        file.getUploadURIs().add(uri2);

        // Then
        assertThat(file.getUploadURIs()).hasSize(2);
        assertThat(file.getUploadURIs()).containsExactly(uri1, uri2);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        UploadingAssetFile file1 = UploadingAssetFile.builder()
                .fileName("test.jpg")
                .mimeType("image/jpeg")
                .uploadToken("token123")
                .minPartSize(1024L)
                .maxPartSize(2048L)
                .build();
        file1.getUploadURIs().add(URI.create("https://example.com/upload/1"));

        UploadingAssetFile file2 = UploadingAssetFile.builder()
                .fileName("test.jpg")
                .mimeType("image/jpeg")
                .uploadToken("token123")
                .minPartSize(1024L)
                .maxPartSize(2048L)
                .build();
        file2.getUploadURIs().add(URI.create("https://example.com/upload/1"));

        UploadingAssetFile file3 = UploadingAssetFile.builder()
                .fileName("different.jpg")
                .mimeType("image/png")
                .uploadToken("different")
                .minPartSize(512L)
                .maxPartSize(1024L)
                .build();

        // Then
        assertThat(file1).isEqualTo(file2);
        assertThat(file1.hashCode()).isEqualTo(file2.hashCode());
        assertThat(file1).isNotEqualTo(file3);
        assertThat(file1.hashCode()).isNotEqualTo(file3.hashCode());
    }
}
