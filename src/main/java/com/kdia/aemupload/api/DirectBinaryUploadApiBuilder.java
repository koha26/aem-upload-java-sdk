package com.kdia.aemupload.api;

import com.kdia.aemupload.api.builder.BaseApiBuilder;
import com.kdia.aemupload.config.ApiServerConfiguration;
import com.kdia.aemupload.api.impl.DirectBinaryUploadApiImpl;

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
