package com.kdiachenko.aemupload.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DamAssetMetadataTest {

    @Test
    void defaultConstructor_shouldCreateEmptyObject() {
        // When
        DamAssetMetadata metadata = new DamAssetMetadata();

        // Then
        assertThat(metadata.getScene7Domain()).isNull();
        assertThat(metadata.getScene7File()).isNull();
        assertThat(metadata.getScene7FileStatus()).isNull();
        assertThat(metadata.getScene7Type()).isNull();
        assertThat(metadata.getFormat()).isNull();
        assertThat(metadata.getSize()).isEqualTo(0L);
    }

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFields() {
        // Given
        String scene7Domain = "domain";
        String scene7File = "file";
        String scene7FileStatus = "status";
        String scene7Type = "type";
        String format = "format";
        long size = 1024L;

        // When
        DamAssetMetadata metadata = new DamAssetMetadata(
                scene7Domain, scene7File, scene7FileStatus, scene7Type, format, size);

        // Then
        assertThat(metadata.getScene7Domain()).isEqualTo(scene7Domain);
        assertThat(metadata.getScene7File()).isEqualTo(scene7File);
        assertThat(metadata.getScene7FileStatus()).isEqualTo(scene7FileStatus);
        assertThat(metadata.getScene7Type()).isEqualTo(scene7Type);
        assertThat(metadata.getFormat()).isEqualTo(format);
        assertThat(metadata.getSize()).isEqualTo(size);
    }

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        String scene7Domain = "domain";
        String scene7File = "file";
        String scene7FileStatus = "status";
        String scene7Type = "type";
        String format = "format";
        long size = 1024L;

        // When
        DamAssetMetadata metadata = DamAssetMetadata.builder()
                .scene7Domain(scene7Domain)
                .scene7File(scene7File)
                .scene7FileStatus(scene7FileStatus)
                .scene7Type(scene7Type)
                .format(format)
                .size(size)
                .build();

        // Then
        assertThat(metadata.getScene7Domain()).isEqualTo(scene7Domain);
        assertThat(metadata.getScene7File()).isEqualTo(scene7File);
        assertThat(metadata.getScene7FileStatus()).isEqualTo(scene7FileStatus);
        assertThat(metadata.getScene7Type()).isEqualTo(scene7Type);
        assertThat(metadata.getFormat()).isEqualTo(format);
        assertThat(metadata.getSize()).isEqualTo(size);
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Given
        DamAssetMetadata metadata = new DamAssetMetadata();
        String scene7Domain = "domain";
        String scene7File = "file";
        String scene7FileStatus = "status";
        String scene7Type = "type";
        String format = "format";
        long size = 1024L;

        // When
        metadata.setScene7Domain(scene7Domain);
        metadata.setScene7File(scene7File);
        metadata.setScene7FileStatus(scene7FileStatus);
        metadata.setScene7Type(scene7Type);
        metadata.setFormat(format);
        metadata.setSize(size);

        // Then
        assertThat(metadata.getScene7Domain()).isEqualTo(scene7Domain);
        assertThat(metadata.getScene7File()).isEqualTo(scene7File);
        assertThat(metadata.getScene7FileStatus()).isEqualTo(scene7FileStatus);
        assertThat(metadata.getScene7Type()).isEqualTo(scene7Type);
        assertThat(metadata.getFormat()).isEqualTo(format);
        assertThat(metadata.getSize()).isEqualTo(size);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        DamAssetMetadata metadata1 = DamAssetMetadata.builder()
                .scene7Domain("domain")
                .scene7File("file")
                .size(1024L)
                .build();

        DamAssetMetadata metadata2 = DamAssetMetadata.builder()
                .scene7Domain("domain")
                .scene7File("file")
                .size(1024L)
                .build();

        DamAssetMetadata metadata3 = DamAssetMetadata.builder()
                .scene7Domain("different")
                .scene7File("different")
                .size(2048L)
                .build();

        // Then
        assertThat(metadata1).isEqualTo(metadata2);
        assertThat(metadata1.hashCode()).isEqualTo(metadata2.hashCode());
        assertThat(metadata1).isNotEqualTo(metadata3);
        assertThat(metadata1.hashCode()).isNotEqualTo(metadata3.hashCode());
    }
}
