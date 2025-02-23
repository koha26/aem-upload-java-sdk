package com.kdia.aemupload.options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteUploadResponse {
    private String fileName;
    private String filePath;
    private String contentType;
}
