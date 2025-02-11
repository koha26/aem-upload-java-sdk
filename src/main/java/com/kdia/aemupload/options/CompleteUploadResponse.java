package com.kdia.aemupload.options;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompleteUploadResponse {
    private String fileName;
    private String filePath;
    private String contentType;
}
