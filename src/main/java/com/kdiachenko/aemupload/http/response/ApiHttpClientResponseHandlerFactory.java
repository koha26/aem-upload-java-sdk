package com.kdiachenko.aemupload.http.response;

import com.kdiachenko.aemupload.http.client.HttpClientSerializer;
import com.kdiachenko.aemupload.internal.http.JacksonHttpClientSerializer;

public interface ApiHttpClientResponseHandlerFactory {

    ApiHttpClientResponseHandlerFactory DEFAULT = new ApiHttpClientResponseHandlerFactory() {
        private final HttpClientSerializer httpClientSerializer = new JacksonHttpClientSerializer();

        @Override
        public <T> ApiHttpClientResponseHandler<T> createHandler(final Class<T> responseType) {
            return new ApiHttpClientResponseHandler<>(responseType, httpClientSerializer);
        }
    };

    static ApiHttpClientResponseHandlerFactory getInstance() {
        return DEFAULT;
    }

    <T> ApiHttpClientResponseHandler<T> createHandler(Class<T> responseType);
}
