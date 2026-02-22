package com.kdiachenko.aemupload.http;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractHttpClient5BuilderFactoryImplTest {

    @Mock
    private HttpClient5BuilderConfigurator configurator;

    @Mock
    private HttpClientBuilder httpClientBuilder;

    private TestHttpClient5BuilderFactoryImpl factory;

    @BeforeEach
    void setUp() {
        factory = new TestHttpClient5BuilderFactoryImpl(configurator);
        factory.setHttpClientBuilder(httpClientBuilder);
        when(configurator.configure(httpClientBuilder)).thenReturn(httpClientBuilder);
    }

    @Test
    @DisplayName("create should call configurator.configure with result of createBuilder")
    void create_shouldCallConfiguratorConfigureWithResultOfCreateBuilder() {
        // Act
        HttpClientBuilder result = factory.create();

        // Assert
        verify(configurator).configure(httpClientBuilder);
        assertThat(result).isEqualTo(httpClientBuilder);
    }

    /**
     * Concrete implementation of AbstractHttpClient5BuilderFactoryImpl for testing
     */
    private static class TestHttpClient5BuilderFactoryImpl extends AbstractHttpClient5BuilderFactoryImpl {
        private HttpClientBuilder httpClientBuilder;

        TestHttpClient5BuilderFactoryImpl(HttpClient5BuilderConfigurator configurator) {
            super(configurator);
        }

        void setHttpClientBuilder(HttpClientBuilder httpClientBuilder) {
            this.httpClientBuilder = httpClientBuilder;
        }

        @Override
        protected HttpClientBuilder createBuilder() {
            return httpClientBuilder;
        }
    }
}
