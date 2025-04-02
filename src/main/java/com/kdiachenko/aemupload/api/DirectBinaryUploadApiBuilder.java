package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.api.builder.BaseApiBuilder;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.api.impl.DirectBinaryUploadApiImpl;

public class DirectBinaryUploadApiBuilder extends BaseApiBuilder<DirectBinaryUploadApiBuilder> {
    protected DirectBinaryUploadApiBuilder(ApiServerConfiguration apiServerConfiguration) {
        super(apiServerConfiguration);
    }

    public static DirectBinaryUploadApiBuilder builder(final ApiServerConfiguration apiServerConfiguration) {
        return new DirectBinaryUploadApiBuilder(apiServerConfiguration);
    }

    public DirectBinaryUploadApi build() {
        return new DirectBinaryUploadApiImpl(buildApiHttpClient(), apiServerConfiguration);
    }
}
