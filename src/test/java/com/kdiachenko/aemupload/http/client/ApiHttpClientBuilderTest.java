package com.kdiachenko.aemupload.http.client;

import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ApiHttpClientBuilderTest {

    @Test
    void builder_shouldUseDefaultsWhenNotProvided() {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        ApiHttpClient client = ApiHttpClientBuilder.builder(httpClient).build();

        assertThat(client).isNotNull();
    }

    @Test
    void builder_shouldUseProvidedMapperAndFactory() {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        HttpClientObjectMapper mapper = mock(HttpClientObjectMapper.class);
        ApiHttpClientResponseHandlerFactory factory = mock(ApiHttpClientResponseHandlerFactory.class);

        ApiHttpClient client = ApiHttpClientBuilder.builder(httpClient)
                .setObjectMapper(mapper)
                .setResponseHandlerFactory(factory)
                .build();

        assertThat(client).isNotNull();
    }
}
