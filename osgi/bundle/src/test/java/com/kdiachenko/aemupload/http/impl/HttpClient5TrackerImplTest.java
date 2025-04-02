package com.kdiachenko.aemupload.http.impl;

import com.kdiachenko.aemupload.stubs.CloseableHttpClientStub;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpClient5TrackerImplTest {
    private HttpClient5TrackerImpl tracker;

    private CloseableHttpClientStub mockHttpClient1;
    private CloseableHttpClientStub mockHttpClient2;

    @BeforeEach
    void setUp() {
        tracker = new HttpClient5TrackerImpl();
        mockHttpClient1 = new CloseableHttpClientStub();
        mockHttpClient2 = new CloseableHttpClientStub();
    }

    @Test
    void testTrack() {
        tracker.track(mockHttpClient1);
        tracker.track(mockHttpClient2);

        List<WeakReference<CloseableHttpClient>> trackedHttpClients = tracker.getTrackedHttpClients();

        assertEquals(2, trackedHttpClients.size());
    }

    @Test
    void shouldClearClientsOnCloseAll() {
        tracker.track(mockHttpClient1);
        tracker.track(mockHttpClient2);

        tracker.closeAll();

        List<WeakReference<CloseableHttpClient>> trackedHttpClients = tracker.getTrackedHttpClients();
        assertEquals(0, trackedHttpClients.size());
    }

    @Test
    void shouldClearClientsOnCloseAllEvenIfTheyAreNull() {
        tracker.track(null);
        tracker.track(null);

        tracker.closeAll();

        List<WeakReference<CloseableHttpClient>> trackedHttpClients = tracker.getTrackedHttpClients();
        assertEquals(0, trackedHttpClients.size());
    }

    @Test
    void shouldCloseClientsOnCloseAll() {
        tracker.track(mockHttpClient1);
        tracker.track(mockHttpClient2);

        tracker.closeAll();

        assertTrue(mockHttpClient1.isClosed());
        assertTrue(mockHttpClient2.isClosed());
    }

    @Test
    void shouldCloseClientsOnCloseAllIfItIsPossible() throws IOException {
        mockHttpClient1 = new CloseableHttpClientStub() {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        };
        tracker.track(mockHttpClient1);
        tracker.track(mockHttpClient2);

        tracker.closeAll();

        List<WeakReference<CloseableHttpClient>> trackedHttpClients = tracker.getTrackedHttpClients();
        assertEquals(0, trackedHttpClients.size());
        assertFalse(mockHttpClient1.isClosed());
        assertTrue(mockHttpClient2.isClosed());
    }

    @Test
    void testWeakReferenceUniqueness() {
        mockHttpClient1 = new CloseableHttpClientStub();
        tracker.track(mockHttpClient1);

        mockHttpClient1 = null; // Remove strong reference
        System.gc(); // Suggest garbage collection

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<WeakReference<CloseableHttpClient>> trackedHttpClients = tracker.getTrackedHttpClients();

        boolean isCleared = trackedHttpClients.stream().allMatch(ref -> ref.get() == null);
        assertTrue(isCleared);
        assertEquals(1, trackedHttpClients.size());
    }

    @Test
    void testDeactivate() {
        tracker.track(mockHttpClient1);
        tracker.track(mockHttpClient2);

        tracker.deactivate();

        List<WeakReference<CloseableHttpClient>> trackedHttpClients = tracker.getTrackedHttpClients();
        assertEquals(0, trackedHttpClients.size());
    }
}
