package com.kdia.aemupload.api;

import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.model.DamAsset;

public interface AssetMetadataApi {
    AssetApiResponse<DamAsset> getAsset(String fullQualifiedAssetId);

    AssetApiResponse<Void> deleteAsset(String fullQualifiedAssetId);
}
