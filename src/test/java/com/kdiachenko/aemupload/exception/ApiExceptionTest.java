package com.kdiachenko.aemupload.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionTest {

    @Test
    void gettersAndHelpers_shouldWork() {
        ApiException ex = new ApiException("msg", 404, "ERR", "raw", new RuntimeException("cause"));

        assertThat(ex.getHttpStatus()).isEqualTo(404);
        assertThat(ex.getErrorCode()).isEqualTo("ERR");
        assertThat(ex.getRawResponse()).isEqualTo("raw");
        assertThat(ex.isClientError()).isTrue();
        assertThat(ex.isServerError()).isFalse();
    }

    @Test
    void isServerError_shouldBeTrueFor5xx() {
        ApiException ex = new ApiException("msg", 503);
        assertThat(ex.isServerError()).isTrue();
        assertThat(ex.isClientError()).isFalse();
    }

    @Test
    void convenienceConstructors_shouldPopulateOptionalFields() {
        ApiException withErrorCode = new ApiException("msg", 400, "ERR_CODE");
        ApiException withRawResponse = new ApiException("msg", 422, "ERR_CODE", "{\"error\":true}");

        assertThat(withErrorCode.getErrorCode()).isEqualTo("ERR_CODE");
        assertThat(withErrorCode.getRawResponse()).isNull();
        assertThat(withRawResponse.getErrorCode()).isEqualTo("ERR_CODE");
        assertThat(withRawResponse.getRawResponse()).isEqualTo("{\"error\":true}");
    }

    @Test
    void helperMethods_shouldReturnFalseForNonErrorStatuses() {
        ApiException ex = new ApiException("msg", 200);

        assertThat(ex.isClientError()).isFalse();
        assertThat(ex.isServerError()).isFalse();
    }

    @Test
    void isServerError_shouldBeFalseAtUpperBoundary() {
        ApiException ex = new ApiException("msg", 600);

        assertThat(ex.isServerError()).isFalse();
    }
}
