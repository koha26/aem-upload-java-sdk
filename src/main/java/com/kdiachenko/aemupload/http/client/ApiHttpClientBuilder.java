package com.kdiachenko.aemupload.http.client;

import com.kdiachenko.aemupload.http.client.impl.ApiHttpClientImpl;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import com.kdiachenko.aemupload.internal.http.JacksonHttpClientSerializer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class ApiHttpClientBuilder {
    private static final HttpClientSerializer DEFAULT_HTTP_CLIENT_SERIALIZER = new JacksonHttpClientSerializer();

    private final CloseableHttpClient httpClient;
    private HttpClientSerializer httpClientSerializer;
    private ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    private ApiHttpClientBuilder(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static ApiHttpClientBuilder builder(final CloseableHttpClient httpClient) {
        return new ApiHttpClientBuilder(httpClient);
    }

    public ApiHttpClientBuilder setSerializer(final HttpClientSerializer httpClientSerializer) {
        this.httpClientSerializer = httpClientSerializer;
        return this;
    }

    public ApiHttpClientBuilder setResponseHandlerFactory(final ApiHttpClientResponseHandlerFactory factory) {
        this.responseHandlerFactory = factory;
        return this;
    }

    public ApiHttpClient build() {
        if (httpClientSerializer == null) {
            setSerializer(DEFAULT_HTTP_CLIENT_SERIALIZER);
        }
        if (responseHandlerFactory == null) {
            setResponseHandlerFactory(ApiHttpClientResponseHandlerFactory.getInstance());
        }
        return new ApiHttpClientImpl(httpClient, httpClientSerializer, responseHandlerFactory);
    }
}
