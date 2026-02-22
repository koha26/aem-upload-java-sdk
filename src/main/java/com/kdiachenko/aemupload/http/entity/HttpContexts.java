package com.kdiachenko.aemupload.http.entity;

import java.util.Map;

/**
 * Predefined HTTP context configurations for common use cases.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ApiHttpResponse<T> response = httpClient.get(url, HttpContexts.AUTHORIZED, responseType);
 * }</pre>
 */
public final class HttpContexts {

    /**
     * Attribute key indicating that authorization is required for the request.
     */
    public static final String AUTHORIZATION_REQUIRED_ATTR = "api.authorization.required";

    /**
     * HTTP context that signals authorization should be added to the request.
     */
    public static final ApiHttpContext AUTHORIZED = ApiHttpContext.builder()
            .attributes(Map.of(AUTHORIZATION_REQUIRED_ATTR, "true"))
            .build();

    /**
     * HTTP context for requests that don't require authorization.
     */
    public static final ApiHttpContext ANONYMOUS = ApiHttpContext.builder()
            .build();

    private HttpContexts() {
        // Utility class - prevent instantiation
    }
}
