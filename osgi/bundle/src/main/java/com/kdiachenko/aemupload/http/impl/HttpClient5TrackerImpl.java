package com.kdiachenko.aemupload.http.impl;

import com.kdiachenko.aemupload.http.HttpClient5Tracker;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * OSGi service implementation of the {@link HttpClient5Tracker} interface that tracks and manages
 * {@link CloseableHttpClient} instances.
 *
 * <p>This class uses a list of weak references to track HTTP clients and
 * close all tracked clients when the service is deactivated.</p>
 *
 * <p>This mechanism of closing all HTTP clients on service deactivation
 * helps prevent memory leaks, particularly in AEMaaCS.</p>
 *
 * @author kostiantyn.diachenko
 */
@Slf4j
@Component(service = HttpClient5Tracker.class, properties = Constants.SERVICE_RANKING + ":Integer=10")
public class HttpClient5TrackerImpl implements HttpClient5Tracker {
    private final List<WeakReference<CloseableHttpClient>> trackedHttpClients = new ArrayList<>();

    @Deactivate
    void deactivate() {
        closeAll();
    }

    @Override
    public synchronized void track(final CloseableHttpClient client) {
        trackedHttpClients.add(new WeakReference<>(client));
    }

    @Override
    public synchronized void closeAll() {
        for (WeakReference<CloseableHttpClient> client : trackedHttpClients) {
            closeQuietly(client.get());
        }

        trackedHttpClients.clear();
    }

    List<WeakReference<CloseableHttpClient>> getTrackedHttpClients() {
        return trackedHttpClients;
    }

    private void closeQuietly(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException exception) {
            log.error("Error closing http client", exception);
        }

    }
}
