package com.kdiachenko.aemupload;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;

public interface SdkApiFactory {

    DirectBinaryUploadApi createDirectBinaryUploadApi();

    AssetFolderApi createAssetFolderApi();

    AssetMetadataApi createAssetMetadataApi();
}
