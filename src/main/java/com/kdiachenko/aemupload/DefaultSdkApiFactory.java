package com.kdiachenko.aemupload;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetFolderApiBuilder;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.AssetMetadataApiBuilder;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApiBuilder;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.HttpClient5BuilderFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class DefaultSdkApiFactory implements SdkApiFactory {

    protected final HttpClient5BuilderFactory httpClient5BuilderFactory;
    protected final ApiServerConfiguration apiServerConfiguration;
    protected CloseableHttpClient closeableHttpClient;

    public DefaultSdkApiFactory(HttpClient5BuilderFactory httpClient5BuilderFactory,
                                ApiServerConfiguration apiServerConfiguration) {
        this.httpClient5BuilderFactory = httpClient5BuilderFactory;
        this.apiServerConfiguration = apiServerConfiguration;
        closeableHttpClient = httpClient5BuilderFactory.create().build();
    }

    @Override
    public DirectBinaryUploadApi createDirectBinaryUploadApi() {
        return DirectBinaryUploadApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(closeableHttpClient)
                .build();
    }

    @Override
    public AssetFolderApi createAssetFolderApi() {
        return AssetFolderApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(closeableHttpClient)
                .build();
    }

    @Override
    public AssetMetadataApi createAssetMetadataApi() {
        return AssetMetadataApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(closeableHttpClient)
                .build();
    }
}
