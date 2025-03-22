package com.kdiachenko.aemupload.http.impl;

import com.kdiachenko.aemupload.http.HttpClient5Tracker;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
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
