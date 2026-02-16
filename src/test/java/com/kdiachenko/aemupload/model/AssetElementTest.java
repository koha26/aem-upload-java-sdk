package com.kdiachenko.aemupload.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssetElementTest {

    @Test
    void defaultConstructor_shouldInitializeClazzList() {
        // When
        AssetElement element = new AssetElement();

        // Then
        assertThat(element.getClazz()).isNotNull();
        assertThat(element.getClazz()).isEmpty();
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Given
        AssetElement element = new AssetElement();
        List<String> clazz = Arrays.asList("class1", "class2");
        AssetEntityProperties properties = new AssetEntityProperties();
        properties.setName("test-asset");

        // When
        element.setClazz(clazz);
        element.setProperties(properties);

        // Then
        assertThat(element.getClazz()).isEqualTo(clazz);
        assertThat(element.getProperties()).isEqualTo(properties);
    }

    @Test
    void clazz_shouldAllowAddingAndRetrievingValues() {
        // Given
        AssetElement element = new AssetElement();

        // When
        element.getClazz().add("class1");
        element.getClazz().add("class2");

        // Then
        assertThat(element.getClazz()).hasSize(2);
        assertThat(element.getClazz()).containsExactly("class1", "class2");
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        // Given
        AssetElement element1 = new AssetElement();
        element1.getClazz().add("class1");
        AssetEntityProperties properties1 = new AssetEntityProperties();
        properties1.setName("test-asset");
        element1.setProperties(properties1);

        AssetElement element2 = new AssetElement();
        element2.getClazz().add("class1");
        AssetEntityProperties properties2 = new AssetEntityProperties();
        properties2.setName("test-asset");
        element2.setProperties(properties2);

        AssetElement element3 = new AssetElement();
        element3.getClazz().add("different-class");
        AssetEntityProperties properties3 = new AssetEntityProperties();
        properties3.setName("different-asset");
        element3.setProperties(properties3);

        // Then
        assertThat(element1).isEqualTo(element2);
        assertThat(element1.hashCode()).isEqualTo(element2.hashCode());
        assertThat(element1).isNotEqualTo(element3);
        assertThat(element1.hashCode()).isNotEqualTo(element3.hashCode());
    }
}
