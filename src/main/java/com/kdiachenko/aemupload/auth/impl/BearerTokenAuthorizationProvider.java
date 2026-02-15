package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.auth.ApiAuthorizationProvider;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

public class BearerTokenAuthorizationProvider implements ApiAuthorizationProvider {

    private final ApiAccessTokenProvider apiAccessTokenProvider;

    public BearerTokenAuthorizationProvider(ApiAccessTokenProvider apiAccessTokenProvider) {
        this.apiAccessTokenProvider = apiAccessTokenProvider;
    }

    @Override
    public void applyAuthorization(final HttpRequest request, final HttpContext httpContext) {
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiAccessTokenProvider.getAccessToken());
    }
}
