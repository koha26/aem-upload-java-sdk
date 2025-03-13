package com.kdia.aemupload;

import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.impl.AssetMetadataApiImpl;

public class AssetMetadataApiBuilder extends BaseApiBuilder<AssetMetadataApiBuilder> {
    protected AssetMetadataApiBuilder(ServerConfiguration serverConfiguration) {
        super(serverConfiguration);
    }

    public AssetMetadataApi build() {
        return new AssetMetadataApiImpl(buildApiHttpClient(), serverConfiguration);
    }
}
