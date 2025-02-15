package com.kdia.aemupload.http;

import com.kdia.aemupload.http.impl.ApiHttpClientResponseHandler;

public interface ApiHttpClientResponseHandlerFactory {
    <T> ApiHttpClientResponseHandler<T> createHandler(Class<T> responseType);
}
