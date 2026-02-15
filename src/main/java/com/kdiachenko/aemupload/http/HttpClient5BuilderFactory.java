package com.kdiachenko.aemupload.http;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

/**
 * Factory for creating configured Apache HttpClient 5 builders.
 *
 * <p>Used by the SDK to obtain a builder instance that can be further customized.</p>
 */
public interface HttpClient5BuilderFactory {

    /**
     * Creates a new HttpClient builder instance.
     *
     * @return a configured HttpClient builder
     */
    HttpClientBuilder create();
}
