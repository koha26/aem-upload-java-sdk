package com.kdia.aemupload.api;

import com.kdia.aemupload.api.builder.BaseApiBuilder;
import com.kdia.aemupload.config.ApiServerConfiguration;
import com.kdia.aemupload.api.impl.AssetFolderApiImpl;

public class AssetFolderApiBuilder extends BaseApiBuilder<AssetFolderApiBuilder> {
    protected AssetFolderApiBuilder(ApiServerConfiguration apiServerConfiguration) {
        super(apiServerConfiguration);
    }

    public static AssetFolderApiBuilder builder(final ApiServerConfiguration apiServerConfiguration) {
        return new AssetFolderApiBuilder(apiServerConfiguration);
    }

    public AssetFolderApi build() {
        return new AssetFolderApiImpl(buildApiHttpClient(), apiServerConfiguration);
    }
}
