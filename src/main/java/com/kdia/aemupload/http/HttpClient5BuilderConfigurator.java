package com.kdia.aemupload.http;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

public interface HttpClient5BuilderConfigurator {
    <T extends HttpClientBuilder> T configure(T clientBuilder);
}
