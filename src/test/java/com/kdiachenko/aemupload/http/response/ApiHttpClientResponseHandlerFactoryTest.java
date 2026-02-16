package com.kdiachenko.aemupload.http.response;

import com.kdiachenko.aemupload.http.client.HttpClientObjectMapper;
import com.kdiachenko.aemupload.http.client.JacksonHttpClientObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiHttpClientResponseHandlerFactoryTest {

    @Test
    void create_shouldReturnFactoryWithDefaultMapper() {
        ApiHttpClientResponseHandlerFactory factory = ApiHttpClientResponseHandlerFactory.create();

        ApiHttpClientResponseHandler<String> handler = factory.createHandler(String.class);

        assertThat(handler).isNotNull();
    }

    @Test
    void create_shouldReturnFactoryWithCustomMapper() {
        HttpClientObjectMapper mapper = new JacksonHttpClientObjectMapper();
        ApiHttpClientResponseHandlerFactory factory = ApiHttpClientResponseHandlerFactory.create(mapper);

        ApiHttpClientResponseHandler<String> handler = factory.createHandler(String.class);

        assertThat(handler).isNotNull();
    }

    @Test
    void defaultFactory_shouldRejectNullMapper() {
        assertThatThrownBy(() -> new DefaultApiHttpClientResponseHandlerFactory(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("serializer must not be null");
    }
}
