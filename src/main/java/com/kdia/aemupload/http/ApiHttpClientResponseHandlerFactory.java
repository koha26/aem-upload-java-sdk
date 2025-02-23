package com.kdia.aemupload.http;

import com.kdia.aemupload.http.impl.DefaultApiHttpClientResponseHandler;

public interface ApiHttpClientResponseHandlerFactory {
    <T> DefaultApiHttpClientResponseHandler<T> createHandler(Class<T> responseType);
}
