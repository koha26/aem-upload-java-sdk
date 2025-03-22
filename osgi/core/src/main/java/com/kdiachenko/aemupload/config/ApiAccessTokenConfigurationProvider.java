package com.kdiachenko.aemupload.config;

import com.kdia.aemupload.config.ApiAccessTokenConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Arrays;
import java.util.List;

@Component(service = ApiAccessTokenConfiguration.class)
@ObjectClassDefinition(name = "AEM Upload SDK - Access Token Configuration")
public class ApiAccessTokenConfigurationProvider implements ApiAccessTokenConfiguration {

    private String localDevelopmentAccessToken;
    private String imsEndpoint;
    private String[] metaScopes;
    private String clientId;
    private String clientSecret;
    private String email;
    private String id;
    private String org;
    private String privateKeyFilePath;
    private String privateKeyContent;
    private int tokenLifeTimeInSec;

    @Activate
    @Modified
    protected void activate(Config config) {
        localDevelopmentAccessToken = config.localDevelopmentAccessToken();
        imsEndpoint = config.imsEndpoint();
        metaScopes = config.metaScopes();
        clientId = config.clientId();
        clientSecret = config.clientSecret();
        email = config.email();
        id = config.id();
        org = config.org();
        privateKeyFilePath = config.privateKeyFilePath();
        privateKeyContent = config.privateKeyContent();
        tokenLifeTimeInSec = Integer.parseInt(config.tokenLifeTimeInSec());
    }

    @Override
    public String getImsEndpoint() {
        return imsEndpoint;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getOrg() {
        return org;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public List<String> getMetaScopes() {
        return Arrays.asList(metaScopes);
    }

    @Override
    public String getPrivateKeyContent() {
        return privateKeyContent;
    }

    @Override
    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    @Override
    public int getTokenLifeTimeInSec() {
        return tokenLifeTimeInSec;
    }

    @Override
    public String getLocalDevelopmentAccessToken() {
        return localDevelopmentAccessToken;
    }

    @ObjectClassDefinition(name = "AEM Upload SDK - Server Configuration",
            description = "This configuration is used to define the destination server configuration")
    public static @interface Config {

        @AttributeDefinition(name = "Destination server schema")
        String localDevelopmentAccessToken();

        @AttributeDefinition(name = "Destination server host")
        String imsEndpoint();

        @AttributeDefinition(name = "Destination server port")
        String[] metaScopes();

        @AttributeDefinition(name = "Destination server port")
        String clientId();

        @AttributeDefinition(name = "Destination server port")
        String clientSecret();

        @AttributeDefinition(name = "Destination server port")
        String email();

        @AttributeDefinition(name = "Destination server port")
        String id();

        @AttributeDefinition(name = "Destination server port")
        String org();

        @AttributeDefinition(name = "Destination server port")
        String privateKeyFilePath();

        @AttributeDefinition(name = "Destination server port")
        String privateKeyContent();

        @AttributeDefinition(name = "Destination server port")
        String tokenLifeTimeInSec();
    }
}
