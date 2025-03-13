package com.kdia.aemupload;

import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.ApiHttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public abstract class BaseApiBuilder<T extends BaseApiBuilder<T>> {
    protected CloseableHttpClient httpClient;
    protected ApiHttpClient apiHttpClient;
    protected ServerConfiguration serverConfiguration;

    protected BaseApiBuilder(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

    @SuppressWarnings("unchecked")
    public T withHttpClient(final CloseableHttpClient client) {
        this.httpClient = client;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withApiHttpClient(final ApiHttpClient client) {
        this.apiHttpClient = client;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withServerConfiguration(final ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
        return (T) this;
    }

    protected ApiHttpClient buildApiHttpClient() {
        if (apiHttpClient == null) {
            if (httpClient == null) {
                apiHttpClient = ApiHttpClientBuilder.builder(HttpClients.createDefault()).build();
            } else {
                apiHttpClient = ApiHttpClientBuilder.builder(httpClient).build();
            }
        }
        return apiHttpClient;
    }
}
