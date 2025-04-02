package com.kdiachenko.aemupload.api.impl;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.AssetElement;
import com.kdiachenko.aemupload.utils.ApiPathNormalizer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static com.kdiachenko.aemupload.http.client.ApiHttpClient.AUTHORIZABLE_API_REQUEST;

@Slf4j
@AllArgsConstructor
public class AssetFolderApiImpl implements AssetFolderApi {

    private final ApiHttpClient apiHttpClient;
    private final ApiServerConfiguration apiServerConfiguration;

    @Override
    public AssetApiResponse<AssetElement> getFolder(final String folder) {
        var requestUrl = apiServerConfiguration.getHostUrl() + ApiPathNormalizer.normalize(folder);
        ApiHttpResponse<AssetElement> response =
                apiHttpClient.get(requestUrl, AUTHORIZABLE_API_REQUEST, AssetElement.class);
        return AssetApiResponse.map(response);
    }

    @Override
    public AssetApiResponse<Void> createFolder(final String folder) {
        var title = folder.contains("/") ? StringUtils.substringAfterLast(folder, "/") : folder;
        var properties = Map.of("title", title);
        return createFolderWithProperties(folder, properties);
    }

    @Override
    public AssetApiResponse<Void> createFolder(final String folder, final Map<String, String> properties) {
        return createFolderWithProperties(folder, properties);
    }

    private AssetApiResponse<Void> createFolderWithProperties(final String folder,
                                                              final Map<String, String> properties) {
        var formData = Map.of("class", "assetFolder", "properties", properties);
        var httpEntity = ApiHttpEntity.builder().body(formData).build();
        var requestUrl = apiServerConfiguration.getHostUrl() + ApiPathNormalizer.normalize(folder);
        ApiHttpResponse<Void> response =
                apiHttpClient.post(requestUrl, httpEntity, AUTHORIZABLE_API_REQUEST, Void.class);
        return AssetApiResponse.map(response);
    }

}
