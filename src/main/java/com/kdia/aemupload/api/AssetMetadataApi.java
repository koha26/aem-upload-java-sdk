package com.kdia.aemupload.api;

import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.model.DamAsset;

import java.util.Map;

public interface AssetMetadataApi {
    AssetApiResponse<DamAsset> getAssetMetadata(String assetPath);

    AssetApiResponse<Void> updateAssetMetadata(String assetPath, Map<String, String> metadata);

    AssetApiResponse<Void> deleteAsset(String assetPath);
}
