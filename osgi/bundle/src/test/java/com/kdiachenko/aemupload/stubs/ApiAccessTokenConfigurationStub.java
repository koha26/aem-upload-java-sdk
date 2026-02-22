package com.kdiachenko.aemupload.stubs;

import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
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
}
