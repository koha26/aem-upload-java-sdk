package com.kdia.aemupload.auth;

import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;

public interface ApiAuthorizationInterceptor extends HttpRequestInterceptor {
    boolean isAuthorizationRequired(final HttpRequest request);
}
