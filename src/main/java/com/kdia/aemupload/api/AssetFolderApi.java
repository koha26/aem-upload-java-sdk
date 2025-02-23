package com.kdia.aemupload.api;

import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.model.AssetElement;

import java.util.Map;

public interface AssetFolderApi {
    AssetApiResponse<AssetElement> getFolder(String folder);

    AssetApiResponse<Void> createFolder(String folder);

    AssetApiResponse<Void> createFolder(String folder, Map<String, String> properties);
}
