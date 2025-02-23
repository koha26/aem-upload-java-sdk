package com.kdia.aemupload.auth.impl;

import com.kdia.aemupload.auth.ApiAccessTokenProvider;
import com.kdia.aemupload.auth.ApiAuthorizationInterceptor;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;

@AllArgsConstructor
public class ApiAuthorizationInterceptorImpl implements ApiAuthorizationInterceptor {

    private ApiAccessTokenProvider apiAccessTokenProvider;

    @Override
    public void process(HttpRequest httpRequest, EntityDetails entityDetails,
                        HttpContext httpContext) {
        if (isAuthorizationRequired(httpRequest) && !httpRequest.containsHeader("Authorization")) {
            httpRequest.setHeader("Authorization", "Bearer " + apiAccessTokenProvider.getAccessToken());
        }
    }

    @Override
    public boolean isAuthorizationRequired(final HttpRequest request) {
        return !request.getRequestUri().startsWith("/ims/exchange/jwt");
    }
}
