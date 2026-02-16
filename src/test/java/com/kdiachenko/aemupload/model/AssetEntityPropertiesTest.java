package com.kdiachenko.aemupload.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AssetEntityPropertiesTest {

    @Test
    void defaultConstructor_shouldInitializeMetadataMap() {
        // When
        AssetEntityProperties properties = new AssetEntityProperties();

        // Then
        assertThat(properties.getMetadata()).isNotNull();
        assertThat(properties.getMetadata()).isEmpty();
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Given
        AssetEntityProperties properties = new AssetEntityProperties();
        String hidden = "false";
        String name = "test-asset";
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", 123);

        // When
        properties.setHidden(hidden);
        properties.setName(name);
        properties.setMetadata(metadata);

        // Then
        assertThat(properties.getHidden()).isEqualTo(hidden);
        assertThat(properties.getName()).isEqualTo(name);
        assertThat(properties.getMetadata()).isEqualTo(metadata);
    }

    @Test
    void metadata_shouldAllowAddingAndRetrievingValues() {
        // Given
        AssetEntityProperties properties = new AssetEntityProperties();

        // When
        properties.getMetadata().put("key1", "value1");
        properties.getMetadata().put("key2", 123);

        // Then
        assertThat(properties.getMetadata()).hasSize(2);
        assertThat(properties.getMetadata()).containsEntry("key1", "value1");
        assertThat(properties.getMetadata()).containsEntry("key2", 123);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        AssetEntityProperties properties1 = new AssetEntityProperties();
        properties1.setHidden("false");
        properties1.setName("test-asset");
        properties1.getMetadata().put("key", "value");

        AssetEntityProperties properties2 = new AssetEntityProperties();
        properties2.setHidden("false");
        properties2.setName("test-asset");
        properties2.getMetadata().put("key", "value");

        AssetEntityProperties properties3 = new AssetEntityProperties();
        properties3.setHidden("true");
        properties3.setName("different-asset");

        // Then
        assertThat(properties1).isEqualTo(properties2);
        assertThat(properties1.hashCode()).isEqualTo(properties2.hashCode());
        assertThat(properties1).isNotEqualTo(properties3);
        assertThat(properties1.hashCode()).isNotEqualTo(properties3.hashCode());
    }
}
