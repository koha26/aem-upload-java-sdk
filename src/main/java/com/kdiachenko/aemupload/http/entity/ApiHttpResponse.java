package com.kdiachenko.aemupload.http.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * HTTP response wrapper used by the SDK internal HTTP client.
 *
 * @param <T> deserialized body type
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiHttpResponse<T> {
    private int status;
    private T body;
    private String errorMessage;

    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }
}
