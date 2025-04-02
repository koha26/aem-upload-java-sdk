package com.kdiachenko.aemupload.provider.impl;

import com.kdia.aemupload.DefaultSdkApiFactory;
import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.api.DirectBinaryUploadApi;
import com.kdia.aemupload.config.ApiServerConfiguration;
import com.kdia.aemupload.http.HttpClient5BuilderFactory;
import com.kdiachenko.aemupload.provider.SdkApiProvider;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * OSGi service that implements the {@link SdkApiProvider} interface.
 * This implementation provides instances of various SDK APIs using {@link DefaultSdkApiFactory}.
 * <p>
 *
 * @author kostiantyn.diachenko
 */
@Component(
        service = SdkApiProvider.class,
        properties = Constants.SERVICE_RANKING + ":Integer=10"
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
