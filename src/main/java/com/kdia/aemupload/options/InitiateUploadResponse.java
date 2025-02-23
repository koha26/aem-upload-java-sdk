package com.kdia.aemupload.options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InitiateUploadResponse {
    private String completeURI;
    private String folderPath;
    @Builder.Default
    private List<UploadingAssetFile> files = new ArrayList<>();
}
