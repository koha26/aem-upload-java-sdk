package com.kdiachenko.aemupload.options;

import lombok.Builder;
import lombok.Value;

/**
 * Options for completing a binary upload.
 * This is the final step in the direct binary upload process.
 */
@Value
@Builder(toBuilder = true)
public class CompleteBinaryUploadOptions {
    /**
     * The URI path for the complete upload endpoint.
     */
    String completeUri;

    /**
     * The name of the file being uploaded.
     */
    String fileName;

    /**
     * The MIME type of the file.
     */
    String mimeType;

    /**
     * The upload token received from initiate upload.
     */
    String uploadToken;

    /**
     * Whether to create a new version if asset exists.
     */
    boolean createVersion;

    /**
     * Label for the version (if creating version).
     */
    String versionLabel;

    /**
     * Comment for the version (if creating version).
     */
    String versionComment;

    /**
     * Whether to replace existing asset.
     */
    boolean replace;

    /**
     * Duration of the upload in milliseconds.
     */
    long uploadDuration;

    /**
     * Size of the file in bytes.
     */
    long fileSize;

    /**
     * Custom builder with validation.
     */
    public static class CompleteBinaryUploadOptionsBuilder {
        /**
         * Builds the options with validation.
         *
         * @return the validated options
         * @throws IllegalStateException if required fields are missing
         */
        public CompleteBinaryUploadOptions build() {
            if (completeUri == null || completeUri.isBlank()) {
                throw new IllegalStateException("completeUri must not be null or blank");
            }
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalStateException("fileName must not be null or blank");
            }
            if (mimeType == null || mimeType.isBlank()) {
                throw new IllegalStateException("mimeType must not be null or blank");
            }
            if (uploadToken == null || uploadToken.isBlank()) {
                throw new IllegalStateException("uploadToken must not be null or blank");
            }
            return new CompleteBinaryUploadOptions(completeUri, fileName, mimeType, uploadToken,
                    createVersion, versionLabel, versionComment, replace, uploadDuration, fileSize);
        }
    }
}
