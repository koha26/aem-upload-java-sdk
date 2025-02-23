package com.kdia.aemupload.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdia.aemupload.http.ApiHttpClientResponseHandlerFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DefaultApiHttpClientResponseHandlerFactory implements ApiHttpClientResponseHandlerFactory {

    public static final ApiHttpClientResponseHandlerFactory INSTANCE = new DefaultApiHttpClientResponseHandlerFactory();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> DefaultApiHttpClientResponseHandler<T> createHandler(final Class<T> responseType) {
        return new DefaultApiHttpClientResponseHandler<>(responseType, objectMapper);
    }
}
