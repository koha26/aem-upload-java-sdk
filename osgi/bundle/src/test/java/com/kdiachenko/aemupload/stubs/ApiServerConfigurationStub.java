package com.kdiachenko.aemupload.stubs;

import com.kdia.aemupload.config.ApiServerConfiguration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiServerConfigurationStub implements ApiServerConfiguration {
    private String schema;
    private String host;
    private String port;
}
