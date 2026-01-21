package com.kdiachenko.aemupload;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetFolderApiBuilder;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.AssetMetadataApiBuilder;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApiBuilder;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.http.HttpClient5BuilderFactory;
import com.kdiachenko.aemupload.utils.FileSplitter;
import com.kdiachenko.aemupload.internal.utils.FileSplitterImpl;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import com.kdiachenko.aemupload.internal.utils.PathNormalizerImpl;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

/**
 * Default implementation of SdkApiFactory.
 * Creates API instances with injected dependencies.
 */
public class DefaultSdkApiFactory implements SdkApiFactory {

    protected final HttpClient5BuilderFactory httpClient5BuilderFactory;
    protected final ApiServerConfiguration apiServerConfiguration;
    protected final FileSplitter fileSplitter;
    protected final PathNormalizer pathNormalizer;

    /**
     * Creates a factory with default implementations of utilities.
     *
     * @param httpClient5BuilderFactory factory for creating HTTP clients
     * @param apiServerConfiguration server configuration
     */
    public DefaultSdkApiFactory(HttpClient5BuilderFactory httpClient5BuilderFactory,
                                ApiServerConfiguration apiServerConfiguration) {
        this(httpClient5BuilderFactory, apiServerConfiguration, new FileSplitterImpl(), new PathNormalizerImpl());
    }

    /**
     * Creates a factory with all dependencies injected.
     *
     * @param httpClient5BuilderFactory factory for creating HTTP clients
     * @param apiServerConfiguration server configuration
     * @param fileSplitter file splitter implementation
     * @param pathNormalizer path normalizer implementation
     */
    public DefaultSdkApiFactory(HttpClient5BuilderFactory httpClient5BuilderFactory,
                                ApiServerConfiguration apiServerConfiguration,
                                FileSplitter fileSplitter,
                                PathNormalizer pathNormalizer) {
        this.httpClient5BuilderFactory = httpClient5BuilderFactory;
        this.apiServerConfiguration = apiServerConfiguration;
        this.fileSplitter = fileSplitter;
        this.pathNormalizer = pathNormalizer;
    }

    /**
     * Creates a CloseableHttpClient instance.
     * Each API can have its own client instance if needed.
     *
     * @return a new HTTP client instance
     */
    protected CloseableHttpClient createHttpClient() {
        return httpClient5BuilderFactory.create().build();
    }

    @Override
    public DirectBinaryUploadApi createDirectBinaryUploadApi() {
        return DirectBinaryUploadApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(createHttpClient())
                .withFileSplitter(fileSplitter)
                .build();
    }

    @Override
    public AssetFolderApi createAssetFolderApi() {
        return AssetFolderApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(createHttpClient())
                .withPathNormalizer(pathNormalizer)
                .build();
    }

    @Override
    public AssetMetadataApi createAssetMetadataApi() {
        return AssetMetadataApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(createHttpClient())
                .withPathNormalizer(pathNormalizer)
                .build();
    }
}
