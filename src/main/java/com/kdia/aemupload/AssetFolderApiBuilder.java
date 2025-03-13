package com.kdia.aemupload;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.impl.AssetFolderApiImpl;

public class AssetFolderApiBuilder extends BaseApiBuilder<AssetFolderApiBuilder> {
    protected AssetFolderApiBuilder(ServerConfiguration serverConfiguration) {
        super(serverConfiguration);
    }

    public AssetFolderApi build() {
        return new AssetFolderApiImpl(buildApiHttpClient(), serverConfiguration);
    }
}
