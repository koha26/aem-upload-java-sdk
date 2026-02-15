package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.api.builder.BaseApiBuilder;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.api.impl.DirectBinaryUploadApiImpl;
import com.kdiachenko.aemupload.utils.FileSplitter;
import com.kdiachenko.aemupload.utils.impl.FileSplitterImpl;

/**
 * Builder for creating {@link DirectBinaryUploadApi} instances.
 *
 * <p>Conceptually, this builder wires together the HTTP client, server configuration,
 * and optional utilities (like file splitting) required for direct binary uploads.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * DirectBinaryUploadApi api = DirectBinaryUploadApiBuilder.builder(serverConfig)
 *     .withHttpClient(httpClient)
 *     .withFileSplitter(new FileSplitterImpl())
 *     .build();
 * }</pre>
 */
public class DirectBinaryUploadApiBuilder extends BaseApiBuilder<DirectBinaryUploadApiBuilder> {
    private FileSplitter fileSplitter;

    protected DirectBinaryUploadApiBuilder(ApiServerConfiguration apiServerConfiguration) {
        super(apiServerConfiguration);
    }

    public static DirectBinaryUploadApiBuilder builder(final ApiServerConfiguration apiServerConfiguration) {
        return new DirectBinaryUploadApiBuilder(apiServerConfiguration);
    }

    public DirectBinaryUploadApiBuilder withFileSplitter(FileSplitter fileSplitter) {
        this.fileSplitter = fileSplitter;
        return this;
    }

    public DirectBinaryUploadApi build() {
        validate();
        if (fileSplitter == null) {
            fileSplitter = new FileSplitterImpl();
        }
        return new DirectBinaryUploadApiImpl(buildApiHttpClient(), apiServerConfiguration, fileSplitter);
    }
}
