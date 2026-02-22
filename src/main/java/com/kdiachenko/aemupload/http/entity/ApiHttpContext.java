package com.kdiachenko.aemupload.http.entity;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request-scoped attributes passed to the HTTP client execution.
 *
 * <p>Used to signal behaviors such as whether authorization is required.</p>
 */
@Data
@Builder
public class ApiHttpContext {
    @Builder.Default
    private Map<String, String> attributes = new LinkedHashMap<>();
}
