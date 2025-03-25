package com.kdiachenko.aemupload.config;

import com.kdia.aemupload.config.ApiAccessTokenConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Arrays;
import java.util.List;

@Component(service = ApiAccessTokenConfiguration.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = ApiAccessTokenConfigurationProvider.Config.class)
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

    @ObjectClassDefinition(name = "AEM Upload SDK - API Access Token Configuration",
            description = "This configuration is used to define the API access token retrieval parameters.")
    public @interface Config {

        @AttributeDefinition(name = "Local development access token")
        String localDevelopmentAccessToken();

        @AttributeDefinition(name = "IMS endpoint (including host)")
        String imsEndpoint();

        @AttributeDefinition(name = "Meta scopes")
        String[] metaScopes();

        @AttributeDefinition(name = "Client ID")
        String clientId();

        @AttributeDefinition(name = "Client secret")
        String clientSecret();

        @AttributeDefinition(name = "Email")
        String email();

        @AttributeDefinition(name = "Configuration ID")
        String id();

        @AttributeDefinition(name = "Organization ID")
        String org();

        @AttributeDefinition(
                name = "Private key file path",
                description = "Path to private key file in the file system. Ignored if privateKeyContent defined."
        )
        String privateKeyFilePath();

        @AttributeDefinition(name = "Private key content")
        String privateKeyContent();

        @AttributeDefinition(name = "Access token life time (in secs)")
        String tokenLifeTimeInSec();
    }
}
