package com.kdiachenko.aemupload.http;

import com.kdiachenko.aemupload.auth.ApiAccessTokenProviderFactory;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * OSGi service that implements the {@link HttpClient5BuilderConfigurator} interface.
 * This class configures an {@link HttpClientBuilder} using a delegate configurator.
 * <p>
 * In this particular case, the delegate configurator is an instance of the {@link DefaultHttpClient5BuilderConfigurator}.
 *
 * @author kostiantyn.diachenko
 */
@Component(
        service = HttpClient5BuilderConfigurator.class,
        properties = Constants.SERVICE_RANKING + ":Integer=10"
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
