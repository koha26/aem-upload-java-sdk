package com.kdiachenko.aemupload.http.response;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ApiHttpClientResponseHandlerFactory {

    ApiHttpClientResponseHandlerFactory DEFAULT = new ApiHttpClientResponseHandlerFactory() {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public <T> ApiHttpClientResponseHandler<T> createHandler(final Class<T> responseType) {
            return new ApiHttpClientResponseHandler<>(responseType, objectMapper);
        }
    };

    static ApiHttpClientResponseHandlerFactory getInstance() {
        return DEFAULT;
    }

    <T> ApiHttpClientResponseHandler<T> createHandler(Class<T> responseType);
}
