package com.kdia.aemupload.options;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
public class InitiateUploadResponse {
    private String completeURI;
    private String folderPath;
    @Builder.Default
    private List<UploadingAssetFile> files = new ArrayList<>();
}
