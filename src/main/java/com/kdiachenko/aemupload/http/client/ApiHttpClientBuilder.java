package com.kdiachenko.aemupload.http.client;

import com.kdiachenko.aemupload.http.client.impl.ApiHttpClientImpl;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import com.kdiachenko.aemupload.internal.http.JacksonHttpClientObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class ApiHttpClientBuilder {
    private static final HttpClientObjectMapper DEFAULT_HTTP_CLIENT_SERIALIZER = new JacksonHttpClientObjectMapper();

    private final CloseableHttpClient httpClient;
    private HttpClientObjectMapper httpClientObjectMapper;
    private ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    private ApiHttpClientBuilder(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static ApiHttpClientBuilder builder(final CloseableHttpClient httpClient) {
        return new ApiHttpClientBuilder(httpClient);
    }

    public ApiHttpClientBuilder setObjectMapper(final HttpClientObjectMapper httpClientObjectMapper) {
        this.httpClientObjectMapper = httpClientObjectMapper;
        return this;
    }

    public ApiHttpClientBuilder setResponseHandlerFactory(final ApiHttpClientResponseHandlerFactory factory) {
        this.responseHandlerFactory = factory;
        return this;
    }

    public ApiHttpClient build() {
        if (httpClientObjectMapper == null) {
            setObjectMapper(DEFAULT_HTTP_CLIENT_SERIALIZER);
        }
        if (responseHandlerFactory == null) {
            setResponseHandlerFactory(ApiHttpClientResponseHandlerFactory.create(httpClientObjectMapper));
        }
        return new ApiHttpClientImpl(httpClient, httpClientObjectMapper, responseHandlerFactory);
    }
}
