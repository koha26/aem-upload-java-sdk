package com.kdia.aemupload.http.client;

import com.kdia.aemupload.http.entity.ApiHttpContext;
import com.kdia.aemupload.http.entity.ApiHttpEntity;
import com.kdia.aemupload.http.entity.ApiHttpResponse;

import java.util.Map;

public interface ApiHttpClient {

    String API_AUTHORIZATION_REQUIRED_REQ_ATTR = "api.authorization.required";

    ApiHttpContext AUTHORIZABLE_API_REQUEST = ApiHttpContext.builder()
            .attributes(Map.of(API_AUTHORIZATION_REQUIRED_REQ_ATTR, "true"))
            .build();

    <T> ApiHttpResponse<T> get(String url, ApiHttpContext apiHttpContext, Class<T> responseType);

    <E, R> ApiHttpResponse<R> post(String url, ApiHttpEntity<E> entity, ApiHttpContext apiHttpContext, Class<R> responseType);

    <E, R> ApiHttpResponse<R> put(String url, ApiHttpEntity<E> entity, ApiHttpContext apiHttpContext, Class<R> responseType);

    default <T> ApiHttpResponse<T> get(String url, Class<T> responseType) {
        return get(url, null, responseType);
    }

    default <E, R> ApiHttpResponse<R> post(String url, ApiHttpEntity<E> entity, Class<R> responseType) {
        return post(url, entity, null, responseType);
    }

    default <E, R> ApiHttpResponse<R> put(String url, ApiHttpEntity<E> entity, Class<R> responseType) {
        return put(url, entity, null, responseType);
    }
}
