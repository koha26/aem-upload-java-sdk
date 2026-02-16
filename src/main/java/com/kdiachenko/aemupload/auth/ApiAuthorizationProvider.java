package com.kdiachenko.aemupload.auth;

import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * Applies authorization information to outgoing HTTP requests.
 *
 * <p>Implementations set appropriate headers (e.g., Bearer token, Basic auth).</p>
 */
public interface ApiAuthorizationProvider {
    /**
     * Applies authorization information to the given HTTP request.
     *
     * @param request the HTTP request to modify
     * @param httpContext request context for additional metadata
     */
    void applyAuthorization(HttpRequest request, HttpContext httpContext);
}
