package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.auth.ApiAuthorizationProvider;
import com.kdiachenko.aemupload.config.AccessTokenAuthConfig;
import com.kdiachenko.aemupload.config.BasicAuthConfig;
import com.kdiachenko.aemupload.config.ServiceCredentialsAuthConfig;
import com.kdiachenko.aemupload.exception.AuthenticationException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationProvidersTest {

    @Test
    void basicAuthorizationProvider_shouldSetHeader() {
        ApiAuthorizationProvider provider = new BasicAuthorizationProvider("user", "pass");
        HttpRequest request = new BasicHttpRequest("GET", "/");

        provider.applyAuthorization(request, new BasicHttpContext());

        assertThat(request.getFirstHeader(HttpHeaders.AUTHORIZATION)).isNotNull();
        assertThat(request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue()).startsWith("Basic ");
    }

    @Test
    void bearerTokenAuthorizationProvider_shouldSetHeader() {
        ApiAccessTokenProvider tokenProvider = () -> "token";
        ApiAuthorizationProvider provider = new BearerTokenAuthorizationProvider(tokenProvider);
        HttpRequest request = new BasicHttpRequest("GET", "/");

        provider.applyAuthorization(request, new BasicHttpContext());

        assertThat(request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue()).isEqualTo("Bearer token");
    }

    @Test
    void bearerTokenAuthorizationProvider_shouldRejectBlankToken() {
        ApiAccessTokenProvider tokenProvider = () -> " ";
        ApiAuthorizationProvider provider = new BearerTokenAuthorizationProvider(tokenProvider);
        HttpRequest request = new BasicHttpRequest("GET", "/");

        assertThatThrownBy(() -> provider.applyAuthorization(request, new BasicHttpContext()))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Access token is null or empty");
    }

    @Test
    void defaultAuthorizationProviderFactory_shouldCreateProviders() {
        DefaultAuthorizationProviderFactory factory = new DefaultAuthorizationProviderFactory();

        assertThat(factory.create(AccessTokenAuthConfig.of("token"))).isInstanceOf(BearerTokenAuthorizationProvider.class);
        assertThat(factory.create(BasicAuthConfig.of("user", "pass"))).isInstanceOf(BasicAuthorizationProvider.class);
        ServiceCredentialsAuthConfig serviceCredentials = ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build();
        assertThat(factory.create(serviceCredentials)).isInstanceOf(BearerTokenAuthorizationProvider.class);
    }

    @Test
    void defaultAuthorizationProviderFactory_shouldRejectUnknownConfig() {
        DefaultAuthorizationProviderFactory factory = new DefaultAuthorizationProviderFactory();

        assertThatThrownBy(() -> factory.create(() -> "Other"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported authentication configuration");
    }
}
