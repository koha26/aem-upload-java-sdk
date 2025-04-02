package com.kdiachenko.aemupload.config.data;

import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import lombok.Value;

import java.util.List;

@Value
public class ApiAccessTokenProperties implements ApiAccessTokenConfiguration {
    String localDevelopmentAccessToken;
    String imsEndpoint;
    List<String> metaScopes;
    String clientId;
    String clientSecret;
    String email;
    String id;
    String org;
    String privateKeyFilePath;
    String privateKeyContent;
    int tokenLifeTimeInSec;
}
