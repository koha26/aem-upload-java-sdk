package com.kdiachenko.aemupload.api.builder;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.client.HttpClientObjectMapper;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BaseApiBuilderTest {

    // Concrete implementation of BaseApiBuilder for testing
    static class TestApiBuilder extends BaseApiBuilder<TestApiBuilder> {
        TestApiBuilder(ApiServerConfiguration apiServerConfiguration) {
            super(apiServerConfiguration);
        }

        // Expose protected fields for testing
        CloseableHttpClient getHttpClient() {
            return httpClient;
        }

        ApiHttpClient getApiHttpClient() {
            return apiHttpClient;
        }

        ApiServerConfiguration getApiServerConfiguration() {
            return apiServerConfiguration;
        }

        ApiHttpClientResponseHandlerFactory getResponseHandlerFactory() {
            return httpClientResponseHandlerFactory;
        }

        HttpClientObjectMapper getHttpClientObjectMapper() {
            return httpClientObjectMapper;
        }

        // Expose protected method for testing
        @Override
        protected ApiHttpClient buildApiHttpClient() {
            return super.buildApiHttpClient();
        }

        void validateBuilder() {
            validate();
        }
    }

    @Mock
    private ApiServerConfiguration apiServerConfiguration;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private ApiHttpClient apiHttpClient;

    @Mock
    private ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    @Mock
    private HttpClientObjectMapper httpClientObjectMapper;

    private TestApiBuilder builder;

    @BeforeEach
    void setUp() {
        when(apiServerConfiguration.getSchema()).thenReturn("https");
        when(apiServerConfiguration.getHost()).thenReturn("example.com");
        when(apiServerConfiguration.getPort()).thenReturn("443");

        builder = new TestApiBuilder(apiServerConfiguration);
    }

    @Test
    void testConstructor() {
        assertThat(builder.getApiServerConfiguration()).isEqualTo(apiServerConfiguration);
    }

    @Test
    void testWithHttpClient() {
        TestApiBuilder result = builder.withHttpClient(httpClient);

        assertThat(result).isSameAs(builder);
        assertThat(builder.getHttpClient()).isEqualTo(httpClient);
    }

    @Test
    void testWithApiHttpClient() {
        TestApiBuilder result = builder.withApiHttpClient(apiHttpClient);

        assertThat(result).isSameAs(builder);
        assertThat(builder.getApiHttpClient()).isEqualTo(apiHttpClient);
    }

    @Test
    void testWithServerConfiguration() {
        ApiServerConfiguration newConfig = new ApiServerConfiguration() {
            @Override
            public String getSchema() {
                return "http";
            }

            @Override
            public String getHost() {
                return "test.com";
            }

            @Override
            public String getPort() {
                return "8080";
            }
        };

        TestApiBuilder result = builder.withServerConfiguration(newConfig);

        assertThat(result).isSameAs(builder);
        assertThat(builder.getApiServerConfiguration()).isEqualTo(newConfig);
    }

    @Test
    void testWithHttpClientObjectMapper() {
        TestApiBuilder result = builder.withApiHttpClientObjectMapper(httpClientObjectMapper);

        assertThat(result).isSameAs(builder);
        assertThat(builder.getHttpClientObjectMapper()).isEqualTo(httpClientObjectMapper);
    }

    @Test
    void testWithResponseHandlerFactory() {
        TestApiBuilder result = builder.withApiHttpClientResponseHandlerFactory(responseHandlerFactory);

        assertThat(result).isSameAs(builder);
        assertThat(builder.getResponseHandlerFactory()).isEqualTo(responseHandlerFactory);
    }

    @Test
    void testBuildApiHttpClient_WithExistingApiHttpClient() {
        builder.withApiHttpClient(apiHttpClient);

        ApiHttpClient result = builder.buildApiHttpClient();

        assertThat(result).isEqualTo(apiHttpClient);
    }

    @Test
    void testBuildApiHttpClient_WithHttpClient() {
        builder.withHttpClient(httpClient);

        ApiHttpClient result = builder.buildApiHttpClient();

        assertThat(result).isNotNull();
    }

    @Test
    void testBuildApiHttpClient_WithDefaultHttpClient() {
        ApiHttpClient result = builder.buildApiHttpClient();

        assertThat(result).isNotNull();
    }

    @Test
    void validate_shouldFailWhenNoHttpClientsProvided() {
        assertThatThrownBy(() -> builder.validateBuilder())
                .isInstanceOf(com.kdiachenko.aemupload.exception.SdkException.class)
                .hasMessageContaining("Either ApiHttpClient or HttpClient must be provided");
    }

    @Test
    void validate_shouldPassWhenHttpClientProvided() {
        builder.withHttpClient(httpClient);

        builder.validateBuilder();
    }

    @Test
    void validate_shouldPassWhenApiHttpClientProvided() {
        builder.withApiHttpClient(apiHttpClient);

        builder.validateBuilder();
    }
}
