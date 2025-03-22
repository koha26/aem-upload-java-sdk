package com.kdia.aemupload.api;

import com.kdia.aemupload.api.builder.BaseApiBuilder;
import com.kdia.aemupload.config.ApiServerConfiguration;
import com.kdia.aemupload.api.impl.AssetMetadataApiImpl;

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
