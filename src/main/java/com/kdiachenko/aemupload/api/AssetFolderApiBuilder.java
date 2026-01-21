package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.api.builder.BaseApiBuilder;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.api.impl.AssetFolderApiImpl;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import com.kdiachenko.aemupload.internal.utils.PathNormalizerImpl;

public class AssetFolderApiBuilder extends BaseApiBuilder<AssetFolderApiBuilder> {
    private PathNormalizer pathNormalizer;

    protected AssetFolderApiBuilder(ApiServerConfiguration apiServerConfiguration) {
        super(apiServerConfiguration);
    }

    public static AssetFolderApiBuilder builder(final ApiServerConfiguration apiServerConfiguration) {
        return new AssetFolderApiBuilder(apiServerConfiguration);
    }

    public AssetFolderApiBuilder withPathNormalizer(PathNormalizer pathNormalizer) {
        this.pathNormalizer = pathNormalizer;
        return this;
    }

    public AssetFolderApi build() {
        if (pathNormalizer == null) {
            pathNormalizer = new PathNormalizerImpl();
        }
        return new AssetFolderApiImpl(buildApiHttpClient(), apiServerConfiguration, pathNormalizer);
    }
}
