package com.kdia.aemupload.impl;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.entity.ApiHttpEntity;
import com.kdia.aemupload.http.entity.ApiHttpResponse;
import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.model.AssetElement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AssetFolderApiImpl implements AssetFolderApi {

    private final ApiHttpClient apiHttpClient;

    @Override
    public AssetApiResponse<AssetElement> getFolder(final String folder) {
        ApiHttpResponse<AssetElement> response = apiHttpClient.get(buildFolderUrl(folder), AssetElement.class);
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

    public AssetApiResponse<Void> createFolderWithProperties(final String folder,
                                                             final Map<String, String> properties) {
        var formData = Map.of("class", "assetFolder", "properties", properties);
        var httpEntity = ApiHttpEntity.builder().body(formData).build();
        ApiHttpResponse<Void> response = apiHttpClient.post(buildFolderUrl(folder), httpEntity, Void.class);
        return AssetApiResponse.map(response);
    }

    private String buildFolderUrl(final String folder) {
        var normalizedFolder = StringUtils.removeStart(folder, "/content/dam");
        return "/api/assets/" + normalizedFolder;
    }
}
