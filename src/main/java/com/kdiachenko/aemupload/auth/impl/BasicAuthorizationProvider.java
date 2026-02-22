package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.auth.ApiAuthorizationProvider;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthorizationProvider implements ApiAuthorizationProvider {
    private final String encodedCredentials;

    public BasicAuthorizationProvider(String username, String password) {
        String credentials = username + ":" + password;
        this.encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void applyAuthorization(final HttpRequest request, final HttpContext httpContext) {
        request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
    }
}
