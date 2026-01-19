package com.kdiachenko.aemupload.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DamAssetTest {

    @Test
    void defaultConstructor_shouldCreateEmptyObject() {
        // When
        DamAsset asset = new DamAsset();

        // Then
        assertThat(asset.getAssetState()).isNull();
        assertThat(asset.isRunDMProcess()).isFalse();
        assertThat(asset.getMetadata()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateObjectWithAllFields() {
        // Given
        String assetState = "processed";
        boolean runDMProcess = true;
        DamAssetMetadata metadata = new DamAssetMetadata();
        metadata.setFormat("image/jpeg");

        // When
        DamAsset asset = new DamAsset(assetState, runDMProcess, metadata);

        // Then
        assertThat(asset.getAssetState()).isEqualTo(assetState);
        assertThat(asset.isRunDMProcess()).isEqualTo(runDMProcess);
        assertThat(asset.getMetadata()).isEqualTo(metadata);
    }

    @Test
    void builder_shouldCreateObjectWithAllFields() {
        // Given
        String assetState = "processed";
        boolean runDMProcess = true;
        DamAssetMetadata metadata = DamAssetMetadata.builder()
                .format("image/jpeg")
                .size(1024L)
                .build();

        // When
        DamAsset asset = DamAsset.builder()
                .assetState(assetState)
                .runDMProcess(runDMProcess)
                .metadata(metadata)
                .build();

        // Then
        assertThat(asset.getAssetState()).isEqualTo(assetState);
        assertThat(asset.isRunDMProcess()).isEqualTo(runDMProcess);
        assertThat(asset.getMetadata()).isEqualTo(metadata);
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Given
        DamAsset asset = new DamAsset();
        String assetState = "processed";
        boolean runDMProcess = true;
        DamAssetMetadata metadata = new DamAssetMetadata();
        metadata.setFormat("image/jpeg");

        // When
        asset.setAssetState(assetState);
        asset.setRunDMProcess(runDMProcess);
        asset.setMetadata(metadata);

        // Then
        assertThat(asset.getAssetState()).isEqualTo(assetState);
        assertThat(asset.isRunDMProcess()).isEqualTo(runDMProcess);
        assertThat(asset.getMetadata()).isEqualTo(metadata);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        DamAssetMetadata metadata1 = DamAssetMetadata.builder()
                .format("image/jpeg")
                .size(1024L)
                .build();

        DamAsset asset1 = DamAsset.builder()
                .assetState("processed")
                .runDMProcess(true)
                .metadata(metadata1)
                .build();

        DamAssetMetadata metadata2 = DamAssetMetadata.builder()
                .format("image/jpeg")
                .size(1024L)
                .build();

        DamAsset asset2 = DamAsset.builder()
                .assetState("processed")
                .runDMProcess(true)
                .metadata(metadata2)
                .build();

        DamAssetMetadata metadata3 = DamAssetMetadata.builder()
                .format("image/png")
                .size(2048L)
                .build();

        DamAsset asset3 = DamAsset.builder()
                .assetState("unprocessed")
                .runDMProcess(false)
                .metadata(metadata3)
                .build();

        // Then
        assertThat(asset1).isEqualTo(asset2);
        assertThat(asset1.hashCode()).isEqualTo(asset2.hashCode());
        assertThat(asset1).isNotEqualTo(asset3);
        assertThat(asset1.hashCode()).isNotEqualTo(asset3.hashCode());
    }
}
