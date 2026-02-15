package com.kdiachenko.aemupload.auth;

import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * HTTP request interceptor that applies SDK authorization when required.
 *
 * <p>Implementations typically inspect the request context to decide
 * whether to add authentication headers.</p>
 */
public interface ApiAuthorizationInterceptor extends HttpRequestInterceptor {
    /**
     * Determines whether authorization should be applied for the given request/context.
     *
     * @param request the HTTP request
     * @param httpContext the HTTP context
     * @return true if authorization should be applied
     */
    boolean isAuthorizationRequired(final HttpRequest request, final HttpContext httpContext);
}
