package com.kdia.aemupload.config;

import org.apache.commons.lang3.StringUtils;

public interface ApiServerConfiguration {
    String getSchema();

    String getHost();

    String getPort();

    default String getHostUrl() {
        return getSchema() + "://" + getHost() + (StringUtils.isEmpty(getPort()) ? "" : ":" + getPort());
    }
}
