package com.kdia.aemupload.http;

import com.kdia.aemupload.expection.ApiHttpClientException;
import com.kdia.aemupload.http.entity.ApiHttpEntity;
import com.kdia.aemupload.http.entity.ApiHttpResponse;

public interface ApiHttpClient {
    <T> ApiHttpResponse<T> get(String url, Class<T> responseType) throws ApiHttpClientException;

    <E, R> ApiHttpResponse<R> post(String url, ApiHttpEntity<E> entity, Class<R> responseType) throws ApiHttpClientException;

    <E, R> ApiHttpResponse<R> put(String url, ApiHttpEntity<E> entity, Class<R> responseType) throws ApiHttpClientException;
}
