package com.kdia.aemupload.options;

import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UploadingAssetFile {
    private String fileName;
    private String mimeType;
    private String uploadToken;
    @Builder.Default
    private List<URI> uploadURIs = new ArrayList<>();
    private long minPartSize;
    private long maxPartSize;
}
