package com.kdiachenko.aemupload.model;

import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssetApiResponseTest {

    @Test
    void success_shouldCreateSuccessResponse() {
        // Given
        String testBody = "Test Body";

        // When
        AssetApiResponse<String> response = AssetApiResponse.success(testBody);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(testBody);
        assertThat(response.getErrorMessage()).isNull();
    }

    @Test
    void fail_shouldCreateFailureResponse() {
        // Given
        String errorMessage = "Error occurred";

        // When
        AssetApiResponse<String> response = AssetApiResponse.fail(errorMessage);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getBody()).isNull();
        assertThat(response.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    void map_shouldMapSuccessfulHttpResponse() {
        // Given
        String testBody = "Test Body";
        ApiHttpResponse<String> httpResponse = ApiHttpResponse.<String>builder()
                .status(200)
                .body(testBody)
                .build();

        // When
        AssetApiResponse<String> response = AssetApiResponse.map(httpResponse);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(testBody);
        assertThat(response.getErrorMessage()).isNull();
    }

    @Test
    void map_shouldMapFailedHttpResponse() {
        // Given
        String errorMessage = "Error occurred";
        ApiHttpResponse<String> httpResponse = ApiHttpResponse.<String>builder()
                .status(400)
                .errorMessage(errorMessage)
                .build();

        // When
        AssetApiResponse<String> response = AssetApiResponse.map(httpResponse);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getBody()).isNull();
        assertThat(response.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        // Given
        AssetApiResponse<String> response = new AssetApiResponse<>(true, "Test", null);

        // When
        response.setSuccess(false);
        response.setBody("Updated");
        response.setErrorMessage("New error");

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getBody()).isEqualTo("Updated");
        assertThat(response.getErrorMessage()).isEqualTo("New error");
    }

    @Test
    void builder_shouldCreateCorrectInstance() {
        // When
        AssetApiResponse<String> response = AssetApiResponse.<String>builder()
                .success(true)
                .body("Builder Test")
                .errorMessage(null)
                .build();

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo("Builder Test");
        assertThat(response.getErrorMessage()).isNull();
    }
}
