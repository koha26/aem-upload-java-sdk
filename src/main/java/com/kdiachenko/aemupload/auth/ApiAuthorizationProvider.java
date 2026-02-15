package com.kdiachenko.aemupload.auth;

import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

public interface ApiAuthorizationProvider {
    void applyAuthorization(HttpRequest request, HttpContext httpContext);
}
