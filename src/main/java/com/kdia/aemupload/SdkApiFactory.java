package com.kdia.aemupload;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.api.DirectBinaryUploadApi;

public interface SdkApiFactory {

    DirectBinaryUploadApi createDirectBinaryUploadApi();

    AssetFolderApi createAssetFolderApi();

    AssetMetadataApi createAssetMetadataApi();
}
