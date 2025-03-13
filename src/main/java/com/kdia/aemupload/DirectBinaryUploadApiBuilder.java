package com.kdia.aemupload;

import com.kdia.aemupload.api.DirectBinaryUploadApi;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.impl.DirectBinaryUploadApiImpl;

public class DirectBinaryUploadApiBuilder extends BaseApiBuilder<DirectBinaryUploadApiBuilder> {
    protected DirectBinaryUploadApiBuilder(ServerConfiguration serverConfiguration) {
        super(serverConfiguration);
    }

    public DirectBinaryUploadApi build() {
        return new DirectBinaryUploadApiImpl(buildApiHttpClient(), serverConfiguration);
    }
}
