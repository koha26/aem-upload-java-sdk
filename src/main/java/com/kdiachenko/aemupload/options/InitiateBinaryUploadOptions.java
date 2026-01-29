package com.kdiachenko.aemupload.options;

import lombok.Builder;
import lombok.Value;

/**
 * Options for initiating a binary upload.
 * This is the first step in the direct binary upload process.
 */
@Value
@Builder(toBuilder = true)
public class InitiateBinaryUploadOptions {
    /**
     * The DAM folder path where the asset will be created.
     * Example: "/content/dam/my-folder"
     */
    String damAssetFolder;

    /**
     * The name of the file being uploaded.
     */
    String fileName;

    /**
     * The size of the file in bytes.
     */
    long fileSize;

    /**
     * Custom builder with validation.
     */
    public static class InitiateBinaryUploadOptionsBuilder {
        /**
         * Builds the options with validation.
         *
         * @return the validated options
         * @throws IllegalStateException if required fields are missing
         */
        public InitiateBinaryUploadOptions build() {
            if (damAssetFolder == null || damAssetFolder.isBlank()) {
                throw new IllegalStateException("damAssetFolder must not be null or blank");
            }
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalStateException("fileName must not be null or blank");
            }
            if (fileSize <= 0) {
                throw new IllegalStateException("fileSize must be greater than 0");
            }
            return new InitiateBinaryUploadOptions(damAssetFolder, fileName, fileSize);
        }
    }
}
