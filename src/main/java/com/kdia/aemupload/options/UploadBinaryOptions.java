package com.kdia.aemupload.options;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
public class UploadBinaryOptions {
    private Path binary;
    @Builder.Default
    private List<URI> uploadURIs = new ArrayList<>();
    private long minPartSize;
    private long maxPartSize;
    private String contentType;
}
