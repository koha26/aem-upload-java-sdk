package com.kdia.aemupload.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdia.aemupload.http.ApiHttpClientResponseHandlerFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ApiHttpClientResponseHandlerFactoryImpl implements ApiHttpClientResponseHandlerFactory {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> ApiHttpClientResponseHandler<T> createHandler(final Class<T> responseType) {
        return new ApiHttpClientResponseHandler<>(responseType, objectMapper);
    }
}
