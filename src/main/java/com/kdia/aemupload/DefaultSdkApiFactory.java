package com.kdia.aemupload;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.api.AssetFolderApiBuilder;
import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.api.AssetMetadataApiBuilder;
import com.kdia.aemupload.api.DirectBinaryUploadApi;
import com.kdia.aemupload.api.DirectBinaryUploadApiBuilder;
import com.kdia.aemupload.config.ApiServerConfiguration;
import com.kdia.aemupload.http.HttpClient5BuilderFactory;
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
