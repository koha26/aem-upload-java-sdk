package com.kdia.aemupload.impl;

import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.expection.ApiHttpClientException;
import com.kdia.aemupload.http.ApiHttpClient;
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
    public AssetApiResponse<DamAsset> getAsset(final String fullQualifiedAssetId) {
        try {
            ApiHttpResponse<DamAsset> response = apiHttpClient.get(buildAssetMetadataUrl(fullQualifiedAssetId), DamAsset.class);
            return AssetApiResponse.<DamAsset>builder()
                    .status(response.getStatus())
                    .body(response.getBody())
                    .build();
        } catch (ApiHttpClientException e) {
            log.error("Failed to get asset metadata {}. Status: {} {}", fullQualifiedAssetId, e.getStatusCode(), e.getStatusText());
            return AssetApiResponse.<DamAsset>builder().status(e.getStatusCode()).build();
        }
    }

    @Override
    public AssetApiResponse<Void> deleteAsset(final String fullQualifiedAssetId) {
        try {
            Map<Object, Object> properties = Map.of(
                    ":operation", "delete"
            );
            ApiHttpResponse<Void> response = apiHttpClient.post(fullQualifiedAssetId, properties, Map.of(), Void.class);
            return AssetApiResponse.<Void>builder()
                    .status(response.getStatus())
                    .build();
        } catch (ApiHttpClientException e) {
            log.error("Failed to delete asset {}. Status: {} {}", fullQualifiedAssetId, e.getStatusCode(), e.getStatusText());
            return AssetApiResponse.<Void>builder().status(e.getStatusCode()).build();
        }
    }

    private String buildAssetMetadataUrl(final String fullQualifiedAssetId) {
        return fullQualifiedAssetId + "/jcr:content.1.json";
    }

}
