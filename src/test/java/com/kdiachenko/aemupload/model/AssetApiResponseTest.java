package com.kdiachenko.aemupload.model;

import com.kdiachenko.aemupload.exception.SdkError;
import com.kdiachenko.aemupload.exception.SdkException;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetApiResponseTest {

    @Test
    void success_shouldCreateSuccessResponse() {
        // Given
        String testBody = "Test Body";

        // When
        AssetApiResponse<String> response = AssetApiResponse.success(testBody);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isFailure()).isFalse();
        assertThat(response.getBody()).isEqualTo(testBody);
        assertThat(response.getErrorMessage()).isNull();
        assertThat(response.getError()).isEmpty();
    }

    @Test
    void fail_shouldCreateFailureResponse() {
        // Given
        String errorMessage = "Error occurred";

        // When
        AssetApiResponse<String> response = AssetApiResponse.fail(SdkError.builder(SdkError.ErrorCode.UNKNOWN_ERROR, errorMessage).build());

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.isFailure()).isTrue();
        assertThat(response.getBody()).isNull();
        assertThat(response.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(response.getError()).isPresent();
    }

    @Test
    void failWithSdkError_shouldCreateFailureResponseWithTypedError() {
        // Given
        SdkError error = SdkError.apiError("API failed", 500);

        // When
        AssetApiResponse<String> response = AssetApiResponse.fail(error);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent();
        assertThat(response.getError().get().getErrorCode()).isEqualTo(SdkError.ErrorCode.API_ERROR);
        assertThat(response.getError().get().getHttpStatus()).isEqualTo(500);
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
    void getOrThrow_shouldReturnBodyOnSuccess() {
        // Given
        AssetApiResponse<String> response = AssetApiResponse.success("value");

        // When
        String result = response.getOrThrow();

        // Then
        assertThat(result).isEqualTo("value");
    }

    @Test
    void getOrThrow_shouldThrowOnFailure() {
        // Given
        SdkError error = SdkError.apiError("API failed", 404);
        AssetApiResponse<String> response = AssetApiResponse.fail(error);

        // Then
        assertThatThrownBy(response::getOrThrow)
                .isInstanceOf(SdkException.class)
                .hasMessageContaining("API failed");
    }

    @Test
    void getOrElse_shouldReturnBodyOnSuccess() {
        // Given
        AssetApiResponse<String> response = AssetApiResponse.success("value");

        // When
        String result = response.getOrElse("default");

        // Then
        assertThat(result).isEqualTo("value");
    }

    @Test
    void getOrElse_shouldReturnDefaultOnFailure() {
        // Given
        AssetApiResponse<String> response = AssetApiResponse.fail(SdkError.builder(SdkError.ErrorCode.UNKNOWN_ERROR, "error").build());

        // When
        String result = response.getOrElse("default");

        // Then
        assertThat(result).isEqualTo("default");
    }

    @Test
    void map_shouldTransformBodyOnSuccess() {
        // Given
        AssetApiResponse<String> response = AssetApiResponse.success("hello");

        // When
        AssetApiResponse<Integer> mapped = response.map(String::length);

        // Then
        assertThat(mapped.isSuccess()).isTrue();
        assertThat(mapped.getBody()).isEqualTo(5);
    }

    @Test
    void map_shouldPreserveErrorOnFailure() {
        // Given
        AssetApiResponse<String> response = AssetApiResponse.fail(SdkError.builder(SdkError.ErrorCode.UNKNOWN_ERROR, "error").build());

        // When
        AssetApiResponse<Integer> mapped = response.map(String::length);

        // Then
        assertThat(mapped.isSuccess()).isFalse();
        assertThat(mapped.getErrorMessage()).isEqualTo("error");
    }

    @Test
    void ifSuccess_shouldExecuteActionOnSuccess() {
        // Given
        AssetApiResponse<String> response = AssetApiResponse.success("value");
        AtomicReference<String> captured = new AtomicReference<>();

        // When
        response.ifSuccess(captured::set);

        // Then
        assertThat(captured.get()).isEqualTo("value");
    }

    @Test
    void ifFailure_shouldExecuteActionOnFailure() {
        // Given
        SdkError error = SdkError.apiError("error", 500);
        AssetApiResponse<String> response = AssetApiResponse.fail(error);
        AtomicBoolean called = new AtomicBoolean(false);

        // When
        response.ifFailure(e -> called.set(true));

        // Then
        assertThat(called.get()).isTrue();
    }

}
