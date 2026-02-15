package com.kdiachenko.aemupload.http.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Container for HTTP request bodies and headers used by the SDK.
 *
 * @param <T> the body type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiHttpEntity<T> {
    private T body;
    @Builder.Default
    private Map<String, String> headers = new LinkedHashMap<>();
}
