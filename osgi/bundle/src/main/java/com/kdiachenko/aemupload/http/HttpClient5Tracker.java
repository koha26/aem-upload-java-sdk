package com.kdiachenko.aemupload.http;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

/**
 * Interface for tracking and managing {@link CloseableHttpClient} instances.
 *
 * @author kostiantyn.diachenko
 */
public interface HttpClient5Tracker {

    /**
     * Tracks the given {@link CloseableHttpClient} instance.
     *
     * @param client the HTTP client to track
     */
    void track(CloseableHttpClient client);

    /**
     * Closes all tracked {@link CloseableHttpClient} instances.
     */
    void closeAll();
}
