package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.DamAsset;

import java.util.Map;

public interface AssetMetadataApi {
    AssetApiResponse<DamAsset> getAssetMetadata(String assetPath);

    AssetApiResponse<Void> updateAssetMetadata(String assetPath, Map<String, String> metadata);

    AssetApiResponse<Void> deleteAsset(String assetPath);
}
