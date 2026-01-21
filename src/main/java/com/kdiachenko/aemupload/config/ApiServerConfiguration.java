package com.kdiachenko.aemupload.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Configuration interface for the AEM server.
 * Intended for OSGi service injection or custom implementations.
 */
public interface ApiServerConfiguration {
    String getSchema();

    String getHost();

    String getPort();

    default String getHostUrl() {
        return getSchema() + "://" + getHost() + (StringUtils.isEmpty(getPort()) ? "" : ":" + getPort());
    }
}
