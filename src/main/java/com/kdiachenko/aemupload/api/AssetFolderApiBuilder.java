package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.api.builder.BaseApiBuilder;
import com.kdiachenko.aemupload.api.impl.AssetFolderApiImpl;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import com.kdiachenko.aemupload.utils.impl.PathNormalizerImpl;

/**
 * Builder for creating {@link AssetFolderApi} instances.
 *
 * <p>Conceptually, this builder assembles the API with HTTP transport,
 * server configuration, and a path normalization strategy.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * AssetFolderApi api = AssetFolderApiBuilder.builder(serverConfig)
 *     .withHttpClient(httpClient)
 *     .withPathNormalizer(new PathNormalizerImpl())
 *     .build();
 * }</pre>
 */
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
        validate();
        if (pathNormalizer == null) {
            pathNormalizer = new PathNormalizerImpl();
        }
        return new AssetFolderApiImpl(buildApiHttpClient(), apiServerConfiguration, pathNormalizer);
    }
}
