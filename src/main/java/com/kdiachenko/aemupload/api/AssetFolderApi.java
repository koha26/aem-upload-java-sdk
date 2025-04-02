package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.AssetElement;

import java.util.Map;

public interface AssetFolderApi {
    AssetApiResponse<AssetElement> getFolder(String folder);

    AssetApiResponse<Void> createFolder(String folder);

    AssetApiResponse<Void> createFolder(String folder, Map<String, String> properties);
}
