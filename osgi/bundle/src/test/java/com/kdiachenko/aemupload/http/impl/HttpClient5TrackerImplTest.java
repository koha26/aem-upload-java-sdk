package com.kdiachenko.aemupload.http.impl;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HttpClient5TrackerImplTest {

    @Test
    void track_shouldAddClient() {
        HttpClient5TrackerImpl tracker = new HttpClient5TrackerImpl();
        CloseableHttpClient client = mock(CloseableHttpClient.class);

        tracker.track(client);

        assertEquals(1, tracker.getTrackedHttpClients().size());
    }

    @Test
    void closeAll_shouldCloseAndClear() throws IOException {
        HttpClient5TrackerImpl tracker = new HttpClient5TrackerImpl();
        CloseableHttpClient client = mock(CloseableHttpClient.class);

        tracker.track(client);
        tracker.closeAll();

        verify(client).close();
        assertEquals(0, tracker.getTrackedHttpClients().size());
    }

    @Test
    void closeAll_shouldHandleIOException() throws IOException {
        HttpClient5TrackerImpl tracker = new HttpClient5TrackerImpl();
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        doThrow(new IOException("boom")).when(client).close();

        tracker.track(client);
        tracker.closeAll();

        assertEquals(0, tracker.getTrackedHttpClients().size());
    }

    @Test
    void deactivate_shouldCloseAll() {
        HttpClient5TrackerImpl tracker = new HttpClient5TrackerImpl();
        tracker.track(null);

        tracker.deactivate();

        assertEquals(0, tracker.getTrackedHttpClients().size());
    }
}
