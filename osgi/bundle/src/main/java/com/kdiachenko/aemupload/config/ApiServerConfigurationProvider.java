package com.kdiachenko.aemupload.config;

import com.kdia.aemupload.config.ApiServerConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi service that provides the configuration for the server of API.
 * This implementation uses the OSGi configuration to define the parameters.
 *
 * <p>The configuration parameters include:</p>
 * <ul>
 *   <li>API server schema</li>
 *   <li>API server host</li>
 *   <li>API server port</li>
 * </ul>
 *
 * <p>The configuration is required for the service to be active, as specified by the
 * {@link ConfigurationPolicy#REQUIRE} policy. That's why this configuration is mandatory for SDK initialization.</p>
 *
 * @author kostiantyn.diachenko
 * @see ApiServerConfiguration
 */
@Component(
        service = ApiServerConfiguration.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(ocd = ApiServerConfigurationProvider.Config.class)
public class ApiServerConfigurationProvider implements ApiServerConfiguration {

    private String schema;
    private String host;
    private String port;

    @Activate
    @Modified
    protected void activate(Config config) {
        this.schema = config.serverSchema();
        this.host = config.serverHost();
        this.port = config.serverPort();
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getPort() {
        return port;
    }

    @ObjectClassDefinition(
            name = "AEM Upload SDK - API Server Configuration",
            description = "This configuration is used to define the API server configuration"
    )
    public @interface Config {

        @AttributeDefinition(name = "API server schema")
        String serverSchema() default "https";

        @AttributeDefinition(name = "API server host")
        String serverHost() default "localhost";

        @AttributeDefinition(name = "API server port")
        String serverPort() default "4502";
    }
}
