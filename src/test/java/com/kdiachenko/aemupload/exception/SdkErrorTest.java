package com.kdiachenko.aemupload.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SdkErrorTest {

    @Test
    void builder_shouldValidateRequiredFields() {
        assertThatThrownBy(() -> SdkError.builder(null, "msg"))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> SdkError.builder(SdkError.ErrorCode.UNKNOWN_ERROR, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void factoryMethods_shouldPopulateFields() {
        Throwable cause = new IllegalStateException("boom");

        SdkError transport = SdkError.transportError("t", cause);
        SdkError api = SdkError.apiError("a", 400, "raw");
        SdkError auth = SdkError.authenticationError("auth");
        SdkError ser = SdkError.serializationError("ser", cause);

        assertThat(transport.getErrorCode()).isEqualTo(SdkError.ErrorCode.TRANSPORT_ERROR);
        assertThat(transport.getCause()).contains(cause);

        assertThat(api.getErrorCode()).isEqualTo(SdkError.ErrorCode.API_ERROR);
        assertThat(api.getHttpStatus()).isEqualTo(400);
        assertThat(api.getRawResponse()).contains("raw");

        assertThat(auth.getErrorCode()).isEqualTo(SdkError.ErrorCode.AUTHENTICATION_ERROR);
        assertThat(ser.getErrorCode()).isEqualTo(SdkError.ErrorCode.SERIALIZATION_ERROR);
    }

    @Test
    void toException_shouldMapByErrorCode() {
        SdkError api = SdkError.apiError("api", 404);
        SdkError auth = SdkError.authenticationError("auth");
        SdkError transport = SdkError.transportError("transport", null);
        SdkError serialization = SdkError.serializationError("ser", null);
        SdkError unknown = SdkError.builder(SdkError.ErrorCode.UNKNOWN_ERROR, "u").build();

        assertThat(api.toException()).isInstanceOf(ApiException.class);
        assertThat(auth.toException()).isInstanceOf(AuthenticationException.class);
        assertThat(transport.toException()).isInstanceOf(TransportException.class);
        assertThat(serialization.toException()).isInstanceOf(SerializationException.class);
        assertThat(unknown.toException()).isInstanceOf(SdkException.class);
    }
}
