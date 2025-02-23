package com.kdia.aemupload;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.api.DirectBinaryUploadApi;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.impl.AssetFolderApiImpl;
import com.kdia.aemupload.impl.AssetMetadataApiImpl;
import com.kdia.aemupload.impl.DirectBinaryUploadApiImpl;

public class SdkApiFactory {

    public static DirectBinaryUploadApi createDirectBinaryUploadApi(final ApiHttpClient apiHttpClient) {
        return new DirectBinaryUploadApiImpl(apiHttpClient);
    }

    public static AssetFolderApi createAssetFolderApi(final ApiHttpClient apiHttpClient) {
        return new AssetFolderApiImpl(apiHttpClient);
    }

    public static AssetMetadataApi createAssetMetadataApi(final ApiHttpClient apiHttpClient) {
        return new AssetMetadataApiImpl(apiHttpClient);
    }
}
