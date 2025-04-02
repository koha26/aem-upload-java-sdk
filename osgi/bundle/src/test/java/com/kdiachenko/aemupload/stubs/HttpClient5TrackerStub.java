package com.kdiachenko.aemupload.stubs;

import com.kdiachenko.aemupload.http.HttpClient5Tracker;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class HttpClient5TrackerStub implements HttpClient5Tracker {
    @Override
    public void track(CloseableHttpClient client) {

    }

    @Override
    public void closeAll() {

    }
}
