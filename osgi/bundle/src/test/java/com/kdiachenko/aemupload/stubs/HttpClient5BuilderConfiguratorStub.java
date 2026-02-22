package com.kdiachenko.aemupload.stubs;

import com.kdiachenko.aemupload.http.HttpClient5BuilderConfigurator;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

public class HttpClient5BuilderConfiguratorStub implements HttpClient5BuilderConfigurator {
    @Override
    public <T extends HttpClientBuilder> T configure(T clientBuilder) {
        return clientBuilder;
    }
}
