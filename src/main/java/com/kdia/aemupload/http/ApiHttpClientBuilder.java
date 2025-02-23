package com.kdia.aemupload.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.http.impl.ApiHttpClientImpl;
import com.kdia.aemupload.http.impl.DefaultApiHttpClientResponseHandlerFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class ApiHttpClientBuilder {
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

    private final CloseableHttpClient httpClient;
    private final ServerConfiguration serverConfiguration;
    private ObjectMapper objectMapper;
    private ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    private ApiHttpClientBuilder(CloseableHttpClient httpClient, ServerConfiguration serverConfiguration) {
        this.httpClient = httpClient;
        this.serverConfiguration = serverConfiguration;
    }

    public ApiHttpClientBuilder builder(final CloseableHttpClient httpClient,
                                        final ServerConfiguration serverConfiguration) {
        return new ApiHttpClientBuilder(httpClient, serverConfiguration);
    }

    public ApiHttpClientBuilder setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public ApiHttpClientBuilder setResponseHandlerFactory(ApiHttpClientResponseHandlerFactory responseHandlerFactory) {
        this.responseHandlerFactory = responseHandlerFactory;
        return this;
    }

    public ApiHttpClient build() {
        if (objectMapper == null) {
            setObjectMapper(DEFAULT_OBJECT_MAPPER);
        }
        if (responseHandlerFactory == null) {
            setResponseHandlerFactory(DefaultApiHttpClientResponseHandlerFactory.INSTANCE);
        }
        return new ApiHttpClientImpl(httpClient, serverConfiguration, objectMapper, responseHandlerFactory);
    }
}
