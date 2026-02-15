package com.kdiachenko.aemupload.config;

import java.util.List;

/**
 * Configuration interface for API access token retrieval.
 * Intended for OSGi service injection or custom implementations.
 */
public interface ApiAccessTokenConfiguration {
    /**
     * Returns the full Adobe IMS token exchange endpoint URL
     * (e.g., https://ims-na1.adobelogin.com/ims/exchange/jwt).
     */
    String getImsEndpoint();

    /**
     * Returns the technical account ID (subject) for JWT claims.
     */
    String getId();

    /**
     * Returns the Adobe organization ID.
     */
    String getOrg();

    /**
     * Returns the Adobe I/O client ID.
     */
    String getClientId();

    /**
     * Returns the Adobe I/O client secret.
     */
    String getClientSecret();

    /**
     * Returns the technical account email, if applicable.
     */
    String getEmail();

    /**
     * Returns the list of Adobe I/O meta scopes.
     */
    List<String> getMetaScopes();

    /**
     * Returns the private key content (PKCS8 PEM) if provided.
     */
    String getPrivateKeyContent();

    /**
     * Returns the private key file path if provided.
     */
    String getPrivateKeyFilePath();

    /**
     * Returns JWT token lifetime in seconds.
     */
    int getTokenLifeTimeInSec();

    /**
     * Returns a local development access token if configured.
     */
    String getLocalDevelopmentAccessToken();
}
