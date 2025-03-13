package com.kdia.aemupload;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.api.DirectBinaryUploadApi;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.ApiHttpClientBuilder;
import com.kdia.aemupload.impl.AssetFolderApiImpl;
import com.kdia.aemupload.impl.AssetMetadataApiImpl;
import com.kdia.aemupload.impl.DirectBinaryUploadApiImpl;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public class SdkApiBuilder {
    private CloseableHttpClient httpClient;
    private ApiHttpClient apiHttpClient;
    private ServerConfiguration serverConfiguration;

    public SdkApiBuilder(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

    public static SdkApiBuilder builder(final ServerConfiguration serverConfiguration) {
        return new SdkApiBuilder(serverConfiguration);
    }

    public SdkApiBuilder withHttpClient(final CloseableHttpClient client) {
        this.httpClient = client;
        return this;
    }

    public SdkApiBuilder withApiHttpClient(final ApiHttpClient client) {
        this.apiHttpClient = client;
        return this;
    }

    public SdkApiBuilder withServerConfiguration(final ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
        return this;
    }

    public DirectBinaryUploadApi build() {
        if (apiHttpClient == null) {
            if (httpClient == null) {
                apiHttpClient = ApiHttpClientBuilder.builder(HttpClients.createDefault()).build();
            } else {
                apiHttpClient = ApiHttpClientBuilder.builder(httpClient).build();
            }
        }
        return new DirectBinaryUploadApiImpl(apiHttpClient, serverConfiguration);
    }

    public AssetFolderApi build2() {
        if (apiHttpClient == null) {
            if (httpClient == null) {
                apiHttpClient = ApiHttpClientBuilder.builder(HttpClients.createDefault()).build();
            } else {
                apiHttpClient = ApiHttpClientBuilder.builder(httpClient).build();
            }
        }
        return new AssetFolderApiImpl(apiHttpClient, serverConfiguration);
    }

    public AssetMetadataApi build4() {
        if (apiHttpClient == null) {
            if (httpClient == null) {
                apiHttpClient = ApiHttpClientBuilder.builder(HttpClients.createDefault()).build();
            } else {
                apiHttpClient = ApiHttpClientBuilder.builder(httpClient).build();
            }
        }
        return new AssetMetadataApiImpl(apiHttpClient, serverConfiguration);
    }
}
