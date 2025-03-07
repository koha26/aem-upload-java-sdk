package com.kdia.aemupload.auth;

import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

public interface ApiAuthorizationInterceptor extends HttpRequestInterceptor {
    boolean isAuthorizationRequired(final HttpRequest request, final HttpContext httpContext);
}
