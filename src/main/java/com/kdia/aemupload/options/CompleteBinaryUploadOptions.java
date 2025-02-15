package com.kdia.aemupload.options;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompleteBinaryUploadOptions {
    private String completeUri;
    private String fileName;
    private String mimeType;
    private String uploadToken;
    private boolean createVersion;
    private String versionLabel;
    private String versionComment;
    private boolean replace;
    private long uploadDuration;
    private long fileSize;
}
