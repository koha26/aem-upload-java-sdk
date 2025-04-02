package com.kdiachenko.aemupload.options;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitiateBinaryUploadOptions {
    private String damAssetFolder;
    private String fileName;
    private long fileSize;
}
