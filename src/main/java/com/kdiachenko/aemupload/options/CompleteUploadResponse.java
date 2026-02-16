package com.kdiachenko.aemupload.options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned when a direct binary upload is completed.
 *
 * <p>Contains file identification details as returned by AEM.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteUploadResponse {
    private String fileName;
    private String filePath;
    private String contentType;
}
