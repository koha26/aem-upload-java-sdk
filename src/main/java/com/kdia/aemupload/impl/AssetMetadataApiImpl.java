package com.kdia.aemupload.impl;

import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.ApiHttpEntity;
import com.kdia.aemupload.http.ApiHttpResponse;
import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.model.DamAsset;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AssetMetadataApiImpl implements AssetMetadataApi {

    private final ApiHttpClient apiHttpClient;

    @Override
    public AssetApiResponse<DamAsset> getAsset(final String assetPath) {
        ApiHttpResponse<DamAsset> response = apiHttpClient.get(buildAssetMetadataUrl(assetPath), DamAsset.class);
        return AssetApiResponse.map(response);
    }

    @Override
    public AssetApiResponse<Void> deleteAsset(final String assetPath) {
        Map<String, Object> properties = Map.of(":operation", "delete");
        var httpEntity = ApiHttpEntity.builder().body(properties).build();
        ApiHttpResponse<Void> response = apiHttpClient.post(assetPath, httpEntity, Void.class);
        return AssetApiResponse.map(response);
    }

    private String buildAssetMetadataUrl(final String fullQualifiedAssetId) {
        return fullQualifiedAssetId + "/jcr:content.1.json";
    }

}
