package com.kdiachenko.aemupload.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SdkExceptionTest {

    @Test
    void sdkExceptionConstructors_shouldSetMessageAndCause() {
        RuntimeException cause = new RuntimeException("cause");
        SdkException ex1 = new SdkException("msg");
        SdkException ex2 = new SdkException("msg", cause);
        SdkException ex3 = new SdkException(cause);

        assertThat(ex1.getMessage()).isEqualTo("msg");
        assertThat(ex2.getCause()).isSameAs(cause);
        assertThat(ex3.getCause()).isSameAs(cause);
    }

    @Test
    void specializedExceptions_shouldExtendSdkException() {
        AuthenticationException auth = new AuthenticationException("auth");
        TransportException transport = new TransportException("transport");
        SerializationException ser = new SerializationException("ser");

        assertThat(auth).isInstanceOf(SdkException.class);
        assertThat(transport).isInstanceOf(SdkException.class);
        assertThat(ser).isInstanceOf(SdkException.class);
    }
}
