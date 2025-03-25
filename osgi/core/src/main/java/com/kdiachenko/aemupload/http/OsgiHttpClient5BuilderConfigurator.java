package com.kdiachenko.aemupload.http;

import com.kdia.aemupload.auth.ApiAccessTokenProviderFactory;
import com.kdia.aemupload.http.DefaultHttpClient5BuilderConfigurator;
import com.kdia.aemupload.http.HttpClient5BuilderConfigurator;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = HttpClient5BuilderConfigurator.class,
        properties = {
                Constants.SERVICE_DESCRIPTION + "=" + "AEM Upload SDK - Default HTTP Client 5 Builder Configurator",
                Constants.SERVICE_RANKING + "=" + "10"
        }
)
public class OsgiHttpClient5BuilderConfigurator implements HttpClient5BuilderConfigurator {

    private final HttpClient5BuilderConfigurator configurator;

    @Activate
    public OsgiHttpClient5BuilderConfigurator(@Reference ApiAccessTokenProviderFactory apiAccessTokenProviderFactory) {
        configurator = new DefaultHttpClient5BuilderConfigurator(apiAccessTokenProviderFactory);
    }

    @Override
    public <T extends HttpClientBuilder> T configure(T clientBuilder) {
        return configurator.configure(clientBuilder);
    }
}
