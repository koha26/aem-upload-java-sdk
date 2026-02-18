package com.kdiachenko.aemupload.http.entity;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable HTTP response wrapper used by the SDK internal HTTP client.
 *
 * @param <T> deserialized body type
 */
@Value
@Builder
public class ApiHttpResponse<T> {
    int status;
    T body;
    String errorMessage;

    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }
}
