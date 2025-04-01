package com.kdiachenko.aemupload.provider;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.api.DirectBinaryUploadApi;

public interface SdkApiProvider {
    DirectBinaryUploadApi getDirectBinaryUploadApi();

    AssetFolderApi getAssetFolderApi();

    AssetMetadataApi getAssetMetadataApi();
}
