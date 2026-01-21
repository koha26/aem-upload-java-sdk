package com.kdiachenko.aemupload.config;

import java.util.List;

/**
 * Configuration interface for API access token retrieval.
 * Intended for OSGi service injection or custom implementations.
 */
public interface ApiAccessTokenConfiguration {
    String getImsEndpoint();

    String getId();

    String getOrg();

    String getClientId();

    String getClientSecret();

    String getEmail();

    List<String> getMetaScopes();

    String getPrivateKeyContent();

    String getPrivateKeyFilePath();

    int getTokenLifeTimeInSec();

    String getLocalDevelopmentAccessToken();
}
