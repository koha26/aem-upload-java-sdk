package com.kdiachenko.aemupload.http;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

/**
 * Base factory implementation that applies a {@link HttpClient5BuilderConfigurator}
 * to a newly created HttpClient builder.
 *
 * <p>Subclasses provide the concrete {@link #createBuilder()} implementation.</p>
 */
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
