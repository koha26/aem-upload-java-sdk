package com.kdia.aemupload.http;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

public abstract class AbstractHttpClient5BuilderFactoryImpl implements HttpClient5BuilderFactory {
    private final HttpClient5BuilderConfigurator configurator;

    public AbstractHttpClient5BuilderFactoryImpl(HttpClient5BuilderConfigurator configurator) {
        this.configurator = configurator;
    }

    @Override
    public HttpClientBuilder create() {
        return configurator.configure(createBuilder());
    }

    protected abstract HttpClientBuilder createBuilder();
}
