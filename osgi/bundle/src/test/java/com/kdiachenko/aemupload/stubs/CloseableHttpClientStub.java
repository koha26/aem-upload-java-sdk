package com.kdiachenko.aemupload.stubs;

import lombok.Getter;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;

import java.io.IOException;

@Getter
public class CloseableHttpClientStub extends CloseableHttpClient {
    private boolean closed;

    @Override
    protected CloseableHttpResponse doExecute(HttpHost target, ClassicHttpRequest request, HttpContext context) throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    @Override
    public void close(CloseMode closeMode) {
        closed = true;
    }
}
