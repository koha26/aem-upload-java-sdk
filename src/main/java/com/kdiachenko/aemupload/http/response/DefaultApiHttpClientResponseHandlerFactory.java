package com.kdiachenko.aemupload.http.response;

import com.kdiachenko.aemupload.http.client.HttpClientObjectMapper;

import java.util.Objects;

/**
 * Default implementation of {@link ApiHttpClientResponseHandlerFactory}.
 */
public final class DefaultApiHttpClientResponseHandlerFactory implements ApiHttpClientResponseHandlerFactory {

    private final HttpClientObjectMapper serializer;

    /**
     * Creates a new factory with the given serializer.
     *
     * @param serializer the serializer to use for response deserialization
     */
    public DefaultApiHttpClientResponseHandlerFactory(HttpClientObjectMapper serializer) {
        this.serializer = Objects.requireNonNull(serializer, "serializer must not be null");
    }

    @Override
    public <T> ApiHttpClientResponseHandler<T> createHandler(Class<T> responseType) {
        return new ApiHttpClientResponseHandler<>(responseType, serializer);
    }
}
