package com.kdia.aemupload.config;

import lombok.Value;

import java.util.List;

@Value
public class ApiAccessTokenProperties {
    String localDevelopmentAccessToken;
    String imsEndpoint;
    List<String> metaScopes;
    String clientId;
    String clientSecret;
    String email;
    String id;
    String org;
    String privateKeyFilePath;
    int tokenLifeTimeInSec;
}
