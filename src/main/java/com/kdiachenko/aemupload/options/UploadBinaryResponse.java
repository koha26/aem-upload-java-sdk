package com.kdiachenko.aemupload.options;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of the binary upload step in the direct binary upload flow.
 *
 * <p>Contains the number of binary chunks that were uploaded.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadBinaryResponse {
    private int chunks;
}
