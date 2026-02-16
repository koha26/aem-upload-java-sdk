package com.kdiachenko.aemupload.api.builder;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.exception.SdkException;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.client.ApiHttpClientBuilder;
import com.kdiachenko.aemupload.http.client.HttpClientObjectMapper;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

/**
 * Base builder for SDK API implementations.
 *
 * <p>Provides shared wiring for HTTP clients, server configuration, and serializers.
 * Concrete builders (e.g., {@link com.kdiachenko.aemupload.api.DirectBinaryUploadApiBuilder})
 * extend this class to assemble their APIs.</p>
 */
public abstract class BaseApiBuilder<T extends BaseApiBuilder<T>> {
    protected CloseableHttpClient httpClient;
    protected ApiHttpClient apiHttpClient;
    protected ApiServerConfiguration apiServerConfiguration;
    protected HttpClientObjectMapper httpClientObjectMapper;
    protected ApiHttpClientResponseHandlerFactory httpClientResponseHandlerFactory;

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
    public T withApiHttpClientObjectMapper(final HttpClientObjectMapper httpClientObjectMapper) {
        this.httpClientObjectMapper = httpClientObjectMapper;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withApiHttpClientResponseHandlerFactory(final ApiHttpClientResponseHandlerFactory factory) {
        this.httpClientResponseHandlerFactory = factory;
        return (T) this;
    }

    protected void validate() {
        if (apiHttpClient == null && httpClient == null) {
            throw new SdkException("Either ApiHttpClient or HttpClient must be provided!");
        }
    }

    protected ApiHttpClient buildApiHttpClient() {
        if (apiHttpClient != null) {
            return apiHttpClient;
        }
        ApiHttpClientBuilder builder = ApiHttpClientBuilder.builder(httpClient);
        if (httpClientObjectMapper != null) {
            builder.setObjectMapper(httpClientObjectMapper);
        }
        if (httpClientResponseHandlerFactory != null) {
            builder.setResponseHandlerFactory(httpClientResponseHandlerFactory);
        }
        return builder.build();
    }
}
