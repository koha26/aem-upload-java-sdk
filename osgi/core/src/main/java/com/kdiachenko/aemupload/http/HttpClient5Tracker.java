package com.kdiachenko.aemupload.http;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public interface HttpClient5Tracker {
    void track(CloseableHttpClient client);

    void closeAll();
}
