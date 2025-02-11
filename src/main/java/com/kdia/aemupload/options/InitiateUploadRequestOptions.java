package com.kdia.aemupload.options;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitiateUploadRequestOptions {
    private String damAssetFolder;
    private String fileName;
    private long fileSize;
}
