package com.kdiachenko.aemupload.http.response;

import com.kdiachenko.aemupload.http.client.HttpClientSerializer;
import com.kdiachenko.aemupload.internal.http.JacksonHttpClientSerializer;

/**
 * Factory for creating HTTP response handlers.
 *
 * <p>Use {@link #create()} or {@link #create(HttpClientSerializer)} to create instances.</p>
 */
public interface ApiHttpClientResponseHandlerFactory {

    /**
     * Creates a new factory with default Jackson serializer.
     *
     * @return a new factory instance
     */
    static ApiHttpClientResponseHandlerFactory create() {
        return create(new JacksonHttpClientSerializer());
    }

    /**
     * Creates a new factory with a custom serializer.
     *
     * @param serializer the serializer to use
     * @return a new factory instance
     */
    static ApiHttpClientResponseHandlerFactory create(HttpClientSerializer serializer) {
        return new DefaultApiHttpClientResponseHandlerFactory(serializer);
    }

    /**
     * Creates a response handler for the given response type.
     *
     * @param responseType the expected response type
     * @param <T> the response type
     * @return a response handler
     */
    <T> ApiHttpClientResponseHandler<T> createHandler(Class<T> responseType);
}
