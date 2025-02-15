package com.kdia.aemupload.config;

import java.util.List;

public interface ApiAccessTokenConfiguration {
    String getImsEndpoint();

    String getId();

    String getOrg();

    String getClientId();

    String getClientSecret();

    List<String> getMetaScopes();

    String getPrivateKeyContent();

    String getPrivateKeyFilePath();

    int getTokenLifeTimeInSec();

    String getLocalDevelopmentAccessToken();
}
