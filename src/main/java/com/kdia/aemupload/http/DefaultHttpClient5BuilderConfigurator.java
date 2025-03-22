package com.kdia.aemupload.http;

import com.kdia.aemupload.auth.ApiAccessTokenProvider;
import com.kdia.aemupload.auth.ApiAccessTokenProviderFactory;
import com.kdia.aemupload.auth.impl.ApiAuthorizationInterceptorImpl;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

public class DefaultHttpClient5BuilderConfigurator implements HttpClient5BuilderConfigurator {
    protected ApiAccessTokenProvider apiAccessTokenProvider;

    public DefaultHttpClient5BuilderConfigurator(ApiAccessTokenProviderFactory apiAccessTokenProviderFactory) {
        apiAccessTokenProvider = apiAccessTokenProviderFactory.create();
    }

    @Override
    public <T extends HttpClientBuilder> T configure(T clientBuilder) {
        clientBuilder.addRequestInterceptorFirst(new ApiAuthorizationInterceptorImpl(apiAccessTokenProvider));
        return clientBuilder;
    }
}
