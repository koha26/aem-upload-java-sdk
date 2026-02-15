package com.kdiachenko.aemupload.api.impl;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.entity.HttpContexts;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.AssetElement;
import com.kdiachenko.aemupload.exception.SdkError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AssetFolderApiImpl implements AssetFolderApi {

    private final ApiHttpClient apiHttpClient;
    private final ApiServerConfiguration apiServerConfiguration;
    private final PathNormalizer pathNormalizer;

    @Override
    public AssetApiResponse<AssetElement> getFolder(final String folder) {
        if (StringUtils.isBlank(folder)) {
            return AssetApiResponse.fail(SdkError.apiError("folder must not be null or blank", 400));
        }
        var requestUrl = apiServerConfiguration.getHostUrl() + pathNormalizer.normalize(folder);
        ApiHttpResponse<AssetElement> response =
                apiHttpClient.get(requestUrl, HttpContexts.AUTHORIZED, AssetElement.class);
        return AssetApiResponse.map(response);
    }

    @Override
    public AssetApiResponse<Void> createFolder(final String folder) {
        if (StringUtils.isBlank(folder)) {
            return AssetApiResponse.fail(SdkError.apiError("folder must not be null or blank", 400));
        }
        var title = folder.contains("/") ? StringUtils.substringAfterLast(folder, "/") : folder;
        var properties = Map.of("title", title);
        return createFolderWithProperties(folder, properties);
    }

    @Override
    public AssetApiResponse<Void> createFolder(final String folder, final Map<String, String> properties) {
        if (StringUtils.isBlank(folder)) {
            return AssetApiResponse.fail(SdkError.apiError("folder must not be null or blank", 400));
        }
        if (properties == null) {
            return AssetApiResponse.fail(SdkError.apiError("properties must not be null", 400));
        }
        return createFolderWithProperties(folder, properties);
    }

    private AssetApiResponse<Void> createFolderWithProperties(final String folder,
                                                              final Map<String, String> properties) {
        var formData = Map.of("class", "assetFolder", "properties", properties);
        var httpEntity = ApiHttpEntity.builder().body(formData).build();
        var requestUrl = apiServerConfiguration.getHostUrl() + pathNormalizer.normalize(folder);
        ApiHttpResponse<Void> response =
                apiHttpClient.post(requestUrl, httpEntity, HttpContexts.AUTHORIZED, Void.class);
        return AssetApiResponse.map(response);
    }

}
