package com.kdia.aemupload.impl;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.expection.ApiHttpClientException;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.ApiHttpResponse;
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
        try {
            ApiHttpResponse<AssetElement> response = apiHttpClient.get(buildFolderUrl(folder), AssetElement.class);
            return AssetApiResponse.<AssetElement>builder()
                    .status(response.getStatus())
                    .body(response.getBody())
                    .build();
        } catch (ApiHttpClientException e) {
            log.error("Failed to get folder {}. Response: {} {}", folder, e.getStatusCode(),
                    e.getResponseBodyAsString());
            return AssetApiResponse.<AssetElement>builder().status(e.getStatusCode()).build();
        }
    }

    @Override
    public AssetApiResponse<Void> createFolder(final String folder) {
        try {
            var title = folder.contains("/") ? StringUtils.substringAfterLast(folder, "/") : folder;
            var properties = Map.of(
                    "class", "assetFolder",
                    "properties", Map.of("title", title)
            );
            ApiHttpResponse<Void> response = apiHttpClient.post(buildFolderUrl(folder), properties, Map.of(), Void.class);
            return AssetApiResponse.<Void>builder()
                    .status(response.getStatus())
                    .build();
        } catch (ApiHttpClientException e) {
            log.error("Failed to create folder {}. Response: {} {}", folder, e.getStatusCode(), e.getStatusText());
            return AssetApiResponse.<Void>builder().status(e.getStatusCode()).build();
        }
    }

    private String buildFolderUrl(final String folder) {
        var normalizedFolder = StringUtils.removeStart(folder, "/content/dam");
        return "/api/assets/" + normalizedFolder;
    }
}
