package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.api.builder.BaseApiBuilder;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.api.impl.AssetFolderApiImpl;

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
