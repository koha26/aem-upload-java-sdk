package com.kdiachenko.aemupload.model;

import com.kdiachenko.aemupload.exception.SdkError;
import com.kdiachenko.aemupload.exception.SdkException;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetApiResponseTest {

    @Test
    void success_shouldCreateSuccessResponse() {
        AssetApiResponse<String> response = AssetApiResponse.success("Test Body");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isFailure()).isFalse();
        assertThat(response.getBody()).isEqualTo("Test Body");
        assertThat(response.getError()).isEmpty();
    }

    @Test
    void fail_shouldCreateFailureResponse() {
        SdkError error = SdkError.builder(SdkError.ErrorCode.UNKNOWN_ERROR, "Error occurred").build();

        AssetApiResponse<String> response = AssetApiResponse.fail(error);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.isFailure()).isTrue();
        assertThat(response.getBody()).isNull();
        assertThat(response.getError()).contains(error);
    }

    @Test
    void map_shouldHandleNullHttpResponse() {
        AssetApiResponse<String> response = AssetApiResponse.map((ApiHttpResponse<String>) null);

        assertThat(response.isFailure()).isTrue();
        assertThat(response.getError()).isPresent();
    }

    @Test
    void map_shouldMapSuccessfulHttpResponse() {
        ApiHttpResponse<String> httpResponse = ApiHttpResponse.<String>builder()
                .status(200)
                .body("Test Body")
                .build();

        AssetApiResponse<String> response = AssetApiResponse.map(httpResponse);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo("Test Body");
    }

    @Test
    void map_shouldMapFailedHttpResponse_withMessage() {
        ApiHttpResponse<String> httpResponse = ApiHttpResponse.<String>builder()
                .status(400)
                .errorMessage("Error occurred")
                .build();

        AssetApiResponse<String> response = AssetApiResponse.map(httpResponse);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isPresent();
        assertThat(response.getError().get().getMessage()).contains("Error occurred");
    }

    @Test
    void map_shouldMapFailedHttpResponse_withoutMessage() {
        ApiHttpResponse<String> httpResponse = ApiHttpResponse.<String>builder()
                .status(500)
                .build();

        AssetApiResponse<String> response = AssetApiResponse.map(httpResponse);

        assertThat(response.isFailure()).isTrue();
        assertThat(response.getError().get().getMessage()).contains("Request failed with status 500");
    }

    @Test
    void getOrThrow_shouldReturnBodyOnSuccess() {
        AssetApiResponse<String> response = AssetApiResponse.success("value");

        assertThat(response.getOrThrow()).isEqualTo("value");
    }

    @Test
    void getOrThrow_shouldThrowOnFailure() {
        SdkError error = SdkError.apiError("API failed", 404);
        AssetApiResponse<String> response = AssetApiResponse.fail(error);

        assertThatThrownBy(response::getOrThrow)
                .isInstanceOf(SdkException.class)
                .hasMessageContaining("API failed");
    }

    @Test
    void getOrElse_shouldReturnBodyOnSuccessAndDefaultOnFailure() {
        AssetApiResponse<String> success = AssetApiResponse.success("value");
        AssetApiResponse<String> failure = AssetApiResponse.fail(
                SdkError.builder(SdkError.ErrorCode.UNKNOWN_ERROR, "error").build());

        assertThat(success.getOrElse("default")).isEqualTo("value");
        assertThat(failure.getOrElse("default")).isEqualTo("default");
    }

    @Test
    void map_and_flatMap_shouldTransformOnSuccessAndPreserveErrorOnFailure() {
        AssetApiResponse<String> success = AssetApiResponse.success("hello");
        AssetApiResponse<String> failure = AssetApiResponse.fail(
                SdkError.builder(SdkError.ErrorCode.UNKNOWN_ERROR, "error").build());

        AssetApiResponse<Integer> mapped = success.map(String::length);
        AssetApiResponse<Integer> flatMapped = success.flatMap(v -> AssetApiResponse.success(v.length() * 2));
        AssetApiResponse<Integer> mappedFailure = failure.map(String::length);
        AssetApiResponse<Integer> flatMappedFailure = failure.flatMap(v -> AssetApiResponse.success(1));

        assertThat(mapped.getBody()).isEqualTo(5);
        assertThat(flatMapped.getBody()).isEqualTo(10);
        assertThat(mappedFailure.isFailure()).isTrue();
        assertThat(flatMappedFailure.isFailure()).isTrue();
    }

    @Test
    void ifSuccess_and_ifFailure_shouldInvokeCallbacks() {
        AtomicReference<String> captured = new AtomicReference<>();
        AtomicBoolean failed = new AtomicBoolean(false);

        AssetApiResponse<String> success = AssetApiResponse.success("value");
        AssetApiResponse<String> failure = AssetApiResponse.fail(SdkError.apiError("error", 500));

        success.ifSuccess(captured::set);
        failure.ifFailure(e -> failed.set(true));

        assertThat(captured.get()).isEqualTo("value");
        assertThat(failed.get()).isTrue();
    }

    @Test
    void ifSuccess_shouldNotInvokeCallbackOnFailure() {
        AssetApiResponse<String> failure = AssetApiResponse.fail(SdkError.apiError("error", 500));
        AtomicBoolean called = new AtomicBoolean(false);

        failure.ifSuccess(v -> called.set(true));

        assertThat(called.get()).isFalse();
    }

    @Test
    void ifFailure_shouldIgnoreNullError() {
        AssetApiResponse<String> response = AssetApiResponse.success("ok");
        AtomicBoolean called = new AtomicBoolean(false);

        response.ifFailure(e -> called.set(true));

        assertThat(called.get()).isFalse();
    }

    @Test
    void ifFailure_shouldIgnoreFailedResponseWithNullError() throws Exception {
        Constructor<AssetApiResponse> constructor = AssetApiResponse.class.getDeclaredConstructor(
                boolean.class, Object.class, SdkError.class);
        constructor.setAccessible(true);
        AssetApiResponse<String> response = constructor.newInstance(false, null, null);
        AtomicBoolean called = new AtomicBoolean(false);

        response.ifFailure(e -> called.set(true));

        assertThat(called.get()).isFalse();
    }
}
