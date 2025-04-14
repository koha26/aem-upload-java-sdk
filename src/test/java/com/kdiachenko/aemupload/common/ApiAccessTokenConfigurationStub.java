package com.kdiachenko.aemupload.common;

import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ApiAccessTokenConfigurationStub implements ApiAccessTokenConfiguration {
    private String localDevelopmentAccessToken;
    private String imsEndpoint;
    private List<String> metaScopes;
    private String clientId;
    private String clientSecret;
    private String email;
    private String id;
    private String org;
    private String privateKeyFilePath;
    private String privateKeyContent;
    private int tokenLifeTimeInSec;

    public ApiAccessTokenConfigurationStub(ApiAccessTokenConfiguration config) {
        this.localDevelopmentAccessToken = config.getLocalDevelopmentAccessToken();
        this.imsEndpoint = config.getLocalDevelopmentAccessToken();
        this.metaScopes = config.getMetaScopes();
        this.clientId = config.getClientId();
        this.clientSecret = config.getClientSecret();
        this.email = config.getEmail();
        this.id = config.getId();
        this.org = config.getOrg();
        this.privateKeyFilePath = config.getPrivateKeyFilePath();
        this.privateKeyContent = config.getPrivateKeyContent();
        this.tokenLifeTimeInSec = config.getTokenLifeTimeInSec();
    }
}
