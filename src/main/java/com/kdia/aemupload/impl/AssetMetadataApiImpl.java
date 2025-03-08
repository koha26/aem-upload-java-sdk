package com.kdia.aemupload.impl;

import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.entity.ApiHttpEntity;
import com.kdia.aemupload.http.entity.ApiHttpResponse;
import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.model.DamAsset;
import com.kdia.aemupload.utils.ApiPathNormalizer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.kdia.aemupload.http.ApiHttpClient.AUTHORIZABLE_API_REQUEST;

@Slf4j
@AllArgsConstructor
public class AssetMetadataApiImpl implements AssetMetadataApi {

    private final ApiHttpClient apiHttpClient;

    @Override
    public AssetApiResponse<DamAsset> getAssetMetadata(final String assetPath) {
        ApiHttpResponse<DamAsset> response = apiHttpClient.get(buildAssetMetadataUrl(assetPath),
                AUTHORIZABLE_API_REQUEST, DamAsset.class);
        return AssetApiResponse.map(response);
    }

    @Override
    public AssetApiResponse<Void> updateAssetMetadata(final String assetPath, final Map<String, String> metadata) {
        var formData = Map.of("class", "asset", "properties", metadata);
        var httpEntity = ApiHttpEntity.builder().body(formData).build();
        ApiHttpResponse<Void> response =
                apiHttpClient.put(ApiPathNormalizer.normalize(assetPath), httpEntity, AUTHORIZABLE_API_REQUEST, Void.class);
        return AssetApiResponse.map(response);
    }

    @Override
    public AssetApiResponse<Void> deleteAsset(final String assetPath) {
        Map<String, Object> properties = Map.of(":operation", "delete");
        var httpEntity = ApiHttpEntity.builder().body(properties).build();
        ApiHttpResponse<Void> response = apiHttpClient.post(ApiPathNormalizer.normalize(assetPath),
                httpEntity, AUTHORIZABLE_API_REQUEST, Void.class);
        return AssetApiResponse.map(response);
    }

    private String buildAssetMetadataUrl(final String assetPath) {
        return assetPath + "/jcr:content/metadata.json";
    }

}
