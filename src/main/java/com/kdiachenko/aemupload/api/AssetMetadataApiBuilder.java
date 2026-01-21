package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.api.builder.BaseApiBuilder;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.api.impl.AssetMetadataApiImpl;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import com.kdiachenko.aemupload.internal.utils.PathNormalizerImpl;

public class AssetMetadataApiBuilder extends BaseApiBuilder<AssetMetadataApiBuilder> {
    private PathNormalizer pathNormalizer;

    protected AssetMetadataApiBuilder(ApiServerConfiguration apiServerConfiguration) {
        super(apiServerConfiguration);
    }

    public static AssetMetadataApiBuilder builder(final ApiServerConfiguration apiServerConfiguration) {
        return new AssetMetadataApiBuilder(apiServerConfiguration);
    }

    public AssetMetadataApiBuilder withPathNormalizer(PathNormalizer pathNormalizer) {
        this.pathNormalizer = pathNormalizer;
        return this;
    }

    public AssetMetadataApi build() {
        if (pathNormalizer == null) {
            pathNormalizer = new PathNormalizerImpl();
        }
        return new AssetMetadataApiImpl(buildApiHttpClient(), apiServerConfiguration, pathNormalizer);
    }
}
