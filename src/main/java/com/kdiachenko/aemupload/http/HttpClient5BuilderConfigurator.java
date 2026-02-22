package com.kdiachenko.aemupload.http;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

/**
 * Callback interface for customizing the Apache HttpClient 5 builder.
 *
 * <p>Use this to apply timeouts, connection pooling, or proxy settings
 * when the SDK constructs its HTTP client.</p>
 */
public interface HttpClient5BuilderConfigurator {
    /**
     * Applies custom configuration to the provided HttpClient builder.
     *
     * @param clientBuilder the builder to configure
     * @param <T> concrete builder type
     * @return the configured builder (usually the same instance)
     */
    <T extends HttpClientBuilder> T configure(T clientBuilder);
}
