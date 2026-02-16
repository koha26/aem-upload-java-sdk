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
}
