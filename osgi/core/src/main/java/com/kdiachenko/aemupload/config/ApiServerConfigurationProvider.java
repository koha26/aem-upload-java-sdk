package com.kdiachenko.aemupload.config;

import com.kdia.aemupload.config.ApiServerConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = ApiServerConfiguration.class)
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

    @ObjectClassDefinition(name = "AEM Upload SDK - Server Configuration",
            description = "This configuration is used to define the destination server configuration")
    public @interface Config {

        @AttributeDefinition(name = "Destination server schema")
        String serverSchema() default "https";

        @AttributeDefinition(name = "Destination server host")
        String serverHost() default "localhost";

        @AttributeDefinition(name = "Destination server port")
        String serverPort() default "4502";
    }
}
