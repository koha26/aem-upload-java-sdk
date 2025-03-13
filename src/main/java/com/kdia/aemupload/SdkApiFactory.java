package com.kdia.aemupload;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.api.DirectBinaryUploadApi;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.impl.AssetFolderApiImpl;
import com.kdia.aemupload.impl.AssetMetadataApiImpl;
import com.kdia.aemupload.impl.DirectBinaryUploadApiImpl;

public class SdkApiFactory {

    public static DirectBinaryUploadApi createDirectBinaryUploadApi(final ApiHttpClient apiHttpClient,
                                                                    final ServerConfiguration serverConfiguration) {
        return new DirectBinaryUploadApiImpl(apiHttpClient, serverConfiguration);
    }

    public static AssetFolderApi createAssetFolderApi(final ApiHttpClient apiHttpClient,
                                                      final ServerConfiguration serverConfiguration) {
        return new AssetFolderApiImpl(apiHttpClient, serverConfiguration);
    }

    public static AssetMetadataApi createAssetMetadataApi(final ApiHttpClient apiHttpClient,
                                                          final ServerConfiguration serverConfiguration) {
        return new AssetMetadataApiImpl(apiHttpClient, serverConfiguration);
    }
}
