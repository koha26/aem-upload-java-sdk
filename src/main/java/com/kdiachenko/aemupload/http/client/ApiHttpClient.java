package com.kdiachenko.aemupload.http.client;

import com.kdiachenko.aemupload.http.entity.ApiHttpContext;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.entity.HttpContexts;

import java.util.Map;

/**
 * HTTP client interface for making API requests.
 *
 * <p>This interface abstracts HTTP operations and can be customized or mocked for testing.</p>
 */
public interface ApiHttpClient {

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
