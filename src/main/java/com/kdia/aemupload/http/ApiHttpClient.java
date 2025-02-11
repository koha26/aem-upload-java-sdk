package com.kdia.aemupload.http;

import com.kdia.aemupload.expection.ApiHttpClientException;

import java.util.Map;

public interface ApiHttpClient {
    <T> ApiHttpResponse<T> get(String url, Class<T> responseType) throws ApiHttpClientException;

    <T> ApiHttpResponse<T> post(String url, Object request, Map<String, String> headers, Class<T> responseType) throws ApiHttpClientException;

    <T> ApiHttpResponse<T> put(String url, Object request, Map<String, String> headers, Class<T> responseType) throws ApiHttpClientException;
}
