package com.kdiachenko.aemupload.http;

import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.auth.ApiAccessTokenProviderFactory;
import com.kdiachenko.aemupload.auth.impl.ApiAuthorizationInterceptorImpl;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultHttpClient5BuilderConfiguratorTest {

    @Mock
    private ApiAccessTokenProviderFactory apiAccessTokenProviderFactory;

    @Mock
    private ApiAccessTokenProvider apiAccessTokenProvider;

    @Mock
    private HttpClientBuilder httpClientBuilder;

    @Captor
    private ArgumentCaptor<ApiAuthorizationInterceptorImpl> interceptorCaptor;

    private DefaultHttpClient5BuilderConfigurator configurator;

    @BeforeEach
    void setUp() {
        when(apiAccessTokenProviderFactory.create()).thenReturn(apiAccessTokenProvider);
        configurator = new DefaultHttpClient5BuilderConfigurator(apiAccessTokenProviderFactory);
    }

    @Test
    @DisplayName("Constructor should initialize apiAccessTokenProvider")
    void constructor_shouldInitializeApiAccessTokenProvider() {
        // Verify that the constructor initializes the apiAccessTokenProvider field
        verify(apiAccessTokenProviderFactory).create();

        // Add a package-default getter to access the apiAccessTokenProvider field
        ApiAccessTokenProvider provider = configurator.apiAccessTokenProvider;
        assertThat(provider).isEqualTo(apiAccessTokenProvider);
    }

    @Test
    @DisplayName("Configure should add ApiAuthorizationInterceptorImpl as first request interceptor")
    void configure_shouldAddApiAuthorizationInterceptorImplAsFirstRequestInterceptor() {
        // Act
        HttpClientBuilder result = configurator.configure(httpClientBuilder);

        // Assert
        verify(httpClientBuilder).addRequestInterceptorFirst(interceptorCaptor.capture());
        ApiAuthorizationInterceptorImpl interceptor = interceptorCaptor.getValue();
        assertThat(interceptor).isNotNull();
        assertThat(interceptor).isInstanceOf(ApiAuthorizationInterceptorImpl.class);

        // Verify that the method returns the same builder instance
        assertThat(result).isEqualTo(httpClientBuilder);
    }
}
