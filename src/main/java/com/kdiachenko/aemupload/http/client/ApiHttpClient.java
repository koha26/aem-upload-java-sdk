package com.kdiachenko.aemupload.http.client;

import com.kdiachenko.aemupload.http.entity.ApiHttpContext;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
/**
 * HTTP client interface for making API requests.
 *
 * <p>This interface abstracts HTTP operations and can be customized or mocked for testing.</p>
 */
public interface ApiHttpClient {

    /**
     * Executes an HTTP GET request.
     *
     * @param url request URL
     * @param apiHttpContext request context (may be null)
     * @param responseType response body type
     * @param <T> response body type
     * @return API HTTP response wrapper
     */
    <T> ApiHttpResponse<T> get(String url, ApiHttpContext apiHttpContext, Class<T> responseType);

    /**
     * Executes an HTTP POST request.
     *
     * @param url request URL
     * @param entity request body and headers
     * @param apiHttpContext request context (may be null)
     * @param responseType response body type
     * @param <E> request body type
     * @param <R> response body type
     * @return API HTTP response wrapper
     */
    <E, R> ApiHttpResponse<R> post(String url, ApiHttpEntity<E> entity, ApiHttpContext apiHttpContext, Class<R> responseType);

    /**
     * Executes an HTTP PUT request.
     *
     * @param url request URL
     * @param entity request body and headers
     * @param apiHttpContext request context (may be null)
     * @param responseType response body type
     * @param <E> request body type
     * @param <R> response body type
     * @return API HTTP response wrapper
     */
    <E, R> ApiHttpResponse<R> put(String url, ApiHttpEntity<E> entity, ApiHttpContext apiHttpContext, Class<R> responseType);

    /**
     * Convenience GET without context.
     */
    default <T> ApiHttpResponse<T> get(String url, Class<T> responseType) {
        return get(url, null, responseType);
    }

    /**
     * Convenience POST without context.
     */
    default <E, R> ApiHttpResponse<R> post(String url, ApiHttpEntity<E> entity, Class<R> responseType) {
        return post(url, entity, null, responseType);
    }

    /**
     * Convenience PUT without context.
     */
    default <E, R> ApiHttpResponse<R> put(String url, ApiHttpEntity<E> entity, Class<R> responseType) {
        return put(url, entity, null, responseType);
    }
}
