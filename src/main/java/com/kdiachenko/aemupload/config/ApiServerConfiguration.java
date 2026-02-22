package com.kdiachenko.aemupload.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Configuration interface for the AEM server.
 * Intended for OSGi service injection or custom implementations.
 */
public interface ApiServerConfiguration {
    /**
     * Returns the URL scheme (e.g., "http" or "https").
     */
    String getSchema();

    /**
     * Returns the server host name.
     */
    String getHost();

    /**
     * Returns the server port, or empty string if not configured.
     */
    String getPort();

    /**
     * Returns the base host URL composed from scheme, host, and port.
     */
    default String getHostUrl() {
        return getSchema() + "://" + getHost() + (StringUtils.isEmpty(getPort()) ? "" : ":" + getPort());
    }
}
