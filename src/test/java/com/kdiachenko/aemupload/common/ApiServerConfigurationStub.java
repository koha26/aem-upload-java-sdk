package com.kdiachenko.aemupload.common;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiServerConfigurationStub implements ApiServerConfiguration {
    private String schema;
    private String host;
    private String port;
}
