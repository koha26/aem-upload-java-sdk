package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiAuthorizationInterceptorImplTest {
    private static final String ACCESS_TOKEN = "dummy-token";

    private final HttpRequest httpRequest = new HttpGet("https://example.com");
    private final HttpContext httpContext = new HttpCoreContext();
    private final EntityDetails entityDetails = new StringEntity("");
    private final ApiAccessTokenProvider stubTokenProvider = () -> ACCESS_TOKEN;
    private final ApiAuthorizationInterceptorImpl interceptor = new ApiAuthorizationInterceptorImpl(stubTokenProvider);

    @Test
    @DisplayName("should add Authorization header if required and not already present")
    void shouldAddAuthorizationHeaderIfRequired() throws ProtocolException {
        initApiAuthorizationRequiredRequestAttribute("true");

        interceptor.process(httpRequest, entityDetails, httpContext);

        Header header = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(header.getValue()).isEqualTo("Bearer " + ACCESS_TOKEN);
    }

    @Test
    @DisplayName("should NOT add Authorization header if already present")
    void shouldNotAddAuthorizationIfAlreadyPresent() throws ProtocolException {
        httpRequest.setHeader(HttpHeaders.AUTHORIZATION, "Bearer 123");
        initApiAuthorizationRequiredRequestAttribute("true");

        interceptor.process(httpRequest, entityDetails, httpContext);

        Header header = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(header.getValue()).isEqualTo("Bearer 123");
    }

    @Test
    @DisplayName("should NOT add Authorization header if not required")
    void shouldNotAddAuthorizationIfNotRequired() throws ProtocolException {
        initApiAuthorizationRequiredRequestAttribute("false");

        interceptor.process(httpRequest, entityDetails, httpContext);

        Header header = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(header).isNull();
    }

    @Test
    @DisplayName("should return true when context attribute is 'true' string")
    void isAuthorizationRequiredReturnsTrueForTrueString() {
        initApiAuthorizationRequiredRequestAttribute("true");

        boolean result = interceptor.isAuthorizationRequired(null, httpContext);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when context attribute is 'false' string")
    void isAuthorizationRequiredReturnsFalseForFalseString() {
        initApiAuthorizationRequiredRequestAttribute("false");

        boolean result = interceptor.isAuthorizationRequired(null, httpContext);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when context attribute is null")
    void isAuthorizationRequiredReturnsFalseForNull() {
        initApiAuthorizationRequiredRequestAttribute(null);

        boolean result = interceptor.isAuthorizationRequired(null, httpContext);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when context attribute is not a String")
    void isAuthorizationRequiredReturnsFalseForNonString() {
        initApiAuthorizationRequiredRequestAttribute(123);

        boolean result = interceptor.isAuthorizationRequired(null, httpContext);

        assertThat(result).isFalse();
    }

    private void initApiAuthorizationRequiredRequestAttribute(Object value) {
        httpContext.setAttribute(ApiHttpClient.API_AUTHORIZATION_REQUIRED_REQ_ATTR, value);
    }
}
