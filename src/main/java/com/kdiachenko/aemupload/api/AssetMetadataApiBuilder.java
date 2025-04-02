package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.api.builder.BaseApiBuilder;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.api.impl.AssetMetadataApiImpl;

public class AssetMetadataApiBuilder extends BaseApiBuilder<AssetMetadataApiBuilder> {
    protected AssetMetadataApiBuilder(ApiServerConfiguration apiServerConfiguration) {
        super(apiServerConfiguration);
    }

    public static AssetMetadataApiBuilder builder(final ApiServerConfiguration apiServerConfiguration) {
        return new AssetMetadataApiBuilder(apiServerConfiguration);
    }

    public AssetMetadataApi build() {
        return new AssetMetadataApiImpl(buildApiHttpClient(), apiServerConfiguration);
    }
}
