package com.kdia.aemupload.http.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdia.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import com.kdia.aemupload.http.client.impl.ApiHttpClientImpl;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class ApiHttpClientBuilder {
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

    private final CloseableHttpClient httpClient;
    private ObjectMapper objectMapper;
    private ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    private ApiHttpClientBuilder(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static ApiHttpClientBuilder builder(final CloseableHttpClient httpClient) {
        return new ApiHttpClientBuilder(httpClient);
    }

    public ApiHttpClientBuilder setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public ApiHttpClientBuilder setResponseHandlerFactory(final ApiHttpClientResponseHandlerFactory factory) {
        this.responseHandlerFactory = factory;
        return this;
    }

    public ApiHttpClient build() {
        if (objectMapper == null) {
            setObjectMapper(DEFAULT_OBJECT_MAPPER);
        }
        if (responseHandlerFactory == null) {
            setResponseHandlerFactory(ApiHttpClientResponseHandlerFactory.getInstance());
        }
        return new ApiHttpClientImpl(httpClient, objectMapper, responseHandlerFactory);
    }
}
