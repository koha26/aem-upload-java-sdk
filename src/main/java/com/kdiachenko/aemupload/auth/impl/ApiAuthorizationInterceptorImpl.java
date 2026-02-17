package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.auth.ApiAuthorizationInterceptor;
import com.kdiachenko.aemupload.auth.ApiAuthorizationProvider;
import com.kdiachenko.aemupload.http.entity.HttpContexts;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

@AllArgsConstructor
public class ApiAuthorizationInterceptorImpl implements ApiAuthorizationInterceptor {

    private ApiAuthorizationProvider apiAuthorizationProvider;

    @Override
    public void process(final HttpRequest httpRequest, final EntityDetails entityDetails,
                        final HttpContext httpContext) {
        if (isAuthorizationRequired(httpRequest, httpContext)
                && !httpRequest.containsHeader(HttpHeaders.AUTHORIZATION)) {
            apiAuthorizationProvider.applyAuthorization(httpRequest, httpContext);
        }
    }

    @Override
    public boolean isAuthorizationRequired(final HttpRequest request, final HttpContext httpContext) {
        return isAuthorizableContext(httpContext);
    }

    private boolean isAuthorizableContext(final HttpContext httpContext) {
        Object attribute = httpContext.getAttribute(HttpContexts.AUTHORIZATION_REQUIRED_ATTR);
        return attribute instanceof String && Boolean.parseBoolean(String.valueOf(attribute));
    }
}
