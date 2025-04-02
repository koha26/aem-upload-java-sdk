package com.kdiachenko.aemupload.http;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * OSGi service that implements the {@link HttpClient5BuilderFactory} interface.
 * This class creates and configures an {@link HttpClientBuilder} instance.
 * <p>
 * The configuration is done using a delegate configurator and a tracker for managing
 * {@link CloseableHttpClient} instances.
 * </p>
 *
 * <p>This class uses the OSGi configuration to define the parameters for the default request configuration.</p>
 *
 * @author kostiantyn.diachenko
 */
@Component(
        service = HttpClient5BuilderFactory.class,
        properties = Constants.SERVICE_RANKING + ":Integer=10"
)
@Designate(ocd = OsgiHttpClient5BuilderFactoryImpl.Config.class)
public class OsgiHttpClient5BuilderFactoryImpl extends AbstractHttpClient5BuilderFactoryImpl {

    private final HttpClient5Tracker httpClient5Tracker;
    private RequestConfig defaultRequestConfig;

    @Activate
    public OsgiHttpClient5BuilderFactoryImpl(@Reference HttpClient5BuilderConfigurator configurator,
                                             @Reference HttpClient5Tracker httpClient5Tracker) {
        super(configurator);
        this.httpClient5Tracker = httpClient5Tracker;
    }

    @Activate
    protected void activate(Config config) {
        defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(config.connectionRequestTimeout(), TimeUnit.MILLISECONDS)
                .setResponseTimeout(config.responseTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    protected HttpClientBuilder createBuilder() {
        return new HttpClientBuilder() {
            @Override
            public CloseableHttpClient build() {
                CloseableHttpClient closeableHttpClient = super.build();
                httpClient5Tracker.track(closeableHttpClient);
                return closeableHttpClient;
            }
        }.setDefaultRequestConfig(defaultRequestConfig);
    }

    RequestConfig getDefaultRequestConfig() {
        return defaultRequestConfig;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @ObjectClassDefinition(name = "AEM Upload SDK - HTTP Client 5 Builder Factory configuration")
    public @interface Config {
        @AttributeDefinition(
                name = "HTTP client connection request timeout (in ms)",
                description = "The connection lease request timeout used when requesting a connection from the connection manager."
        )
        int connectionRequestTimeout() default 60_000;

        @AttributeDefinition(
                name = "HTTP client response timeout (in ms)",
                description = "Determines the timeout until arrival of a response from the opposite endpoint."
        )
        int responseTimeout() default 60_000;
    }
}
