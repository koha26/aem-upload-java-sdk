package com.kdiachenko.aemupload.options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Response from the initiate-upload step of the direct binary upload flow.
 *
 * <p>Includes the completion URI, the target folder path, and file upload details.</p>
 */
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
