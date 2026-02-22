package com.kdiachenko.aemupload.api.impl;

import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.exception.SdkError;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.entity.HttpContexts;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.DamAsset;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AssetMetadataApiImpl implements AssetMetadataApi {

    private final ApiHttpClient apiHttpClient;
    private final ApiServerConfiguration apiServerConfiguration;
    private final PathNormalizer pathNormalizer;

    @Override
    public AssetApiResponse<DamAsset> getAssetMetadata(final String assetPath) {
        if (StringUtils.isBlank(assetPath)) {
            return AssetApiResponse.fail(SdkError.apiError("assetPath must not be null or blank", 400));
        }
        ApiHttpResponse<DamAsset> response = apiHttpClient.get(buildAssetMetadataUrl(assetPath),
                HttpContexts.AUTHORIZED, DamAsset.class);
        return AssetApiResponse.map(response);
    }

    @Override
    public AssetApiResponse<Void> updateAssetMetadata(final String assetPath, final Map<String, String> metadata) {
        if (StringUtils.isBlank(assetPath)) {
            return AssetApiResponse.fail(SdkError.apiError("assetPath must not be null or blank", 400));
        }
        if (metadata == null) {
            return AssetApiResponse.fail(SdkError.apiError("metadata must not be null", 400));
        }
        var formData = Map.of("class", "asset", "properties", metadata);
        var httpEntity = ApiHttpEntity.builder().body(formData).build();
        var requestUrl = apiServerConfiguration.getHostUrl() + pathNormalizer.normalize(assetPath);
        ApiHttpResponse<Void> response =
                apiHttpClient.put(requestUrl, httpEntity, HttpContexts.AUTHORIZED, Void.class);
        return AssetApiResponse.map(response);
    }

    @Override
    public AssetApiResponse<Void> deleteAsset(final String assetPath) {
        if (StringUtils.isBlank(assetPath)) {
            return AssetApiResponse.fail(SdkError.apiError("assetPath must not be null or blank", 400));
        }
        Map<String, Object> properties = Map.of(":operation", "delete");
        var httpEntity = ApiHttpEntity.builder().body(properties).build();
        var requestUrl = apiServerConfiguration.getHostUrl() + pathNormalizer.normalize(assetPath);
        ApiHttpResponse<Void> response = apiHttpClient.post(requestUrl,
                httpEntity, HttpContexts.AUTHORIZED, Void.class);
        return AssetApiResponse.map(response);
    }

    private String buildAssetMetadataUrl(final String assetPath) {
        return apiServerConfiguration.getHostUrl() + pathNormalizer.normalize(assetPath);
    }

}
