package com.kdiachenko.aemupload.provider.impl;

import com.kdiachenko.aemupload.DefaultSdkApiFactory;
import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.HttpClient5BuilderFactory;
import com.kdiachenko.aemupload.provider.SdkApiProvider;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * OSGi service that implements the {@link SdkApiProvider} interface.
 * This implementation provides instances of various SDK APIs using {@link DefaultSdkApiFactory}.
 *
 * @author kostiantyn.diachenko
 * @deprecated Use {@link AemUploadSdkServiceImpl} instead, which provides better lifecycle
 *             management and supports multiple authentication types via a single configuration.
 */
@Deprecated(forRemoval = true)
@Component(
        service = SdkApiProvider.class,
        properties = Constants.SERVICE_RANKING + ":Integer=5",
        enabled = false // Disabled by default - use AemUploadSdkServiceImpl instead
)
public class SdkApiProviderImpl implements SdkApiProvider {

    private final HttpClient5BuilderFactory httpClient5BuilderFactory;
    private final ApiServerConfiguration apiServerConfiguration;

    private DirectBinaryUploadApi directBinaryUploadApi;
    private AssetFolderApi assetFolderApi;
    private AssetMetadataApi assetMetadataApi;

    @Activate
    public SdkApiProviderImpl(@Reference HttpClient5BuilderFactory httpClient5BuilderFactory,
                              @Reference ApiServerConfiguration apiServerConfiguration) {
        this.httpClient5BuilderFactory = httpClient5BuilderFactory;
        this.apiServerConfiguration = apiServerConfiguration;
    }

    @Activate
    protected void activate() {
        DefaultSdkApiFactory sdkApiFactory = new DefaultSdkApiFactory(httpClient5BuilderFactory, apiServerConfiguration);
        directBinaryUploadApi = sdkApiFactory.createDirectBinaryUploadApi();
        assetFolderApi = sdkApiFactory.createAssetFolderApi();
        assetMetadataApi = sdkApiFactory.createAssetMetadataApi();
    }

    @Override
    public DirectBinaryUploadApi getDirectBinaryUploadApi() {
        return directBinaryUploadApi;
    }

    @Override
    public AssetFolderApi getAssetFolderApi() {
        return assetFolderApi;
    }

    @Override
    public AssetMetadataApi getAssetMetadataApi() {
        return assetMetadataApi;
    }
}
