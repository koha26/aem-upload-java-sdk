package com.kdia.aemupload.options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadingAssetFile {
    private String fileName;
    private String mimeType;
    private String uploadToken;
    @Builder.Default
    private List<URI> uploadURIs = new ArrayList<>();
    private long minPartSize;
    private long maxPartSize;
}
