package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiAuthorizationInterceptorImplTest {

    private static final String ACCESS_TOKEN = "dummy-token";

    private final ApiAccessTokenProvider stubTokenProvider = () -> ACCESS_TOKEN;

    private final ApiAuthorizationInterceptorImpl interceptor = new ApiAuthorizationInterceptorImpl(stubTokenProvider);

    @Test
    @DisplayName("should add Authorization header if required and not already present")
    void shouldAddAuthorizationHeaderIfRequired() {
        // Arrange
        HttpRequest request = mock(HttpRequest.class);
        HttpContext context = mock(HttpContext.class);
        EntityDetails entityDetails = mock(EntityDetails.class);

        when(request.containsHeader(HttpHeaders.AUTHORIZATION)).thenReturn(false);
        when(context.getAttribute(ApiHttpClient.API_AUTHORIZATION_REQUIRED_REQ_ATTR)).thenReturn("true");

        // Act
        interceptor.process(request, entityDetails, context);

        // Assert
        verify(request).setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
    }

    @Test
    @DisplayName("should NOT add Authorization header if already present")
    void shouldNotAddAuthorizationIfAlreadyPresent() {
        HttpRequest request = mock(HttpRequest.class);
        HttpContext context = mock(HttpContext.class);
        EntityDetails entityDetails = mock(EntityDetails.class);

        when(request.containsHeader(HttpHeaders.AUTHORIZATION)).thenReturn(true);
        when(context.getAttribute(ApiHttpClient.API_AUTHORIZATION_REQUIRED_REQ_ATTR)).thenReturn("true");

        interceptor.process(request, entityDetails, context);

        verify(request, never()).setHeader(eq(HttpHeaders.AUTHORIZATION), anyString());
    }

    @Test
    @DisplayName("should NOT add Authorization header if not required")
    void shouldNotAddAuthorizationIfNotRequired() {
        HttpRequest request = mock(HttpRequest.class);
        HttpContext context = mock(HttpContext.class);
        EntityDetails entityDetails = mock(EntityDetails.class);

        when(request.containsHeader(HttpHeaders.AUTHORIZATION)).thenReturn(false);
        when(context.getAttribute(ApiHttpClient.API_AUTHORIZATION_REQUIRED_REQ_ATTR)).thenReturn("false");

        interceptor.process(request, entityDetails, context);

        verify(request, never()).setHeader(eq(HttpHeaders.AUTHORIZATION), anyString());
    }

    @Test
    @DisplayName("should return true when context attribute is 'true' string")
    void isAuthorizationRequiredReturnsTrueForTrueString() {
        HttpContext context = mock(HttpContext.class);
        when(context.getAttribute(ApiHttpClient.API_AUTHORIZATION_REQUIRED_REQ_ATTR)).thenReturn("true");

        boolean result = interceptor.isAuthorizationRequired(null, context);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when context attribute is 'false' string")
    void isAuthorizationRequiredReturnsFalseForFalseString() {
        HttpContext context = mock(HttpContext.class);
        when(context.getAttribute(ApiHttpClient.API_AUTHORIZATION_REQUIRED_REQ_ATTR)).thenReturn("false");

        boolean result = interceptor.isAuthorizationRequired(null, context);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when context attribute is null")
    void isAuthorizationRequiredReturnsFalseForNull() {
        HttpContext context = mock(HttpContext.class);
        when(context.getAttribute(ApiHttpClient.API_AUTHORIZATION_REQUIRED_REQ_ATTR)).thenReturn(null);

        boolean result = interceptor.isAuthorizationRequired(null, context);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when context attribute is not a String")
    void isAuthorizationRequiredReturnsFalseForNonString() {
        HttpContext context = mock(HttpContext.class);
        when(context.getAttribute(ApiHttpClient.API_AUTHORIZATION_REQUIRED_REQ_ATTR)).thenReturn(123);

        boolean result = interceptor.isAuthorizationRequired(null, context);

        assertThat(result).isFalse();
    }
}
