package com.kdiachenko.aemupload.api.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.client.ApiHttpClientBuilder;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.util.Optional;

public abstract class BaseApiBuilder<T extends BaseApiBuilder<T>> {
    protected CloseableHttpClient httpClient;
    protected ApiHttpClient apiHttpClient;
    protected ApiServerConfiguration apiServerConfiguration;
    protected ObjectMapper objectMapper;
    protected ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    protected BaseApiBuilder(ApiServerConfiguration apiServerConfiguration) {
        this.apiServerConfiguration = apiServerConfiguration;
    }

    @SuppressWarnings("unchecked")
    public T withHttpClient(final CloseableHttpClient client) {
        this.httpClient = client;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withApiHttpClient(final ApiHttpClient client) {
        this.apiHttpClient = client;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withServerConfiguration(final ApiServerConfiguration apiServerConfiguration) {
        this.apiServerConfiguration = apiServerConfiguration;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setResponseHandlerFactory(final ApiHttpClientResponseHandlerFactory responseHandlerFactory) {
        this.responseHandlerFactory = responseHandlerFactory;
        return (T) this;
    }

    protected ApiHttpClient buildApiHttpClient() {
        if (apiHttpClient != null) {
            return apiHttpClient;
        }
        httpClient = Optional.ofNullable(httpClient).orElseGet(HttpClients::createDefault);
        return ApiHttpClientBuilder.builder(httpClient)
                .setObjectMapper(objectMapper)
                .setResponseHandlerFactory(responseHandlerFactory)
                .build();
    }
}
