package com.kdiachenko.aemupload.options;

import lombok.Builder;
import lombok.Value;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Options for uploading binary content to cloud storage.
 *
 * <p>This is the second step in the direct binary upload process and uses the
 * upload URIs returned by the initiate step.</p>
 */
@Value
@Builder(toBuilder = true)
public class UploadBinaryOptions {
    /**
     * The path to the binary file to upload.
     */
    Path binary;

    /**
     * The list of URIs to upload binary parts to.
     */
    @Builder.Default
    List<URI> uploadURIs = Collections.emptyList();

    /**
     * The minimum part size for chunked upload.
     */
    long minPartSize;

    /**
     * The maximum part size for chunked upload.
     */
    long maxPartSize;

    /**
     * The content type (MIME type) of the binary.
     */
    String contentType;

    /**
     * Custom builder with validation.
     */
    public static class UploadBinaryOptionsBuilder {
        private List<URI> uploadURIs = new ArrayList<>();

        public UploadBinaryOptionsBuilder uploadURIs(List<URI> uploadUris) {
            this.uploadURIs = uploadUris != null ? new ArrayList<>(uploadUris) : new ArrayList<>();
            return this;
        }

        /**
         * Builds the options with validation.
         *
         * @return the validated options
         * @throws IllegalStateException if required fields are missing
         */
        public UploadBinaryOptions build() {
            if (binary == null) {
                throw new IllegalStateException("binary path must not be null");
            }
            if (uploadURIs == null || uploadURIs.isEmpty()) {
                throw new IllegalStateException("uploadURIs must not be null or empty");
            }
            if (maxPartSize <= 0) {
                throw new IllegalStateException("maxPartSize must be greater than 0");
            }
            if (contentType == null || contentType.isBlank()) {
                throw new IllegalStateException("contentType must not be null or blank");
            }
            return new UploadBinaryOptions(binary, Collections.unmodifiableList(uploadURIs),
                    minPartSize, maxPartSize, contentType);
        }
    }
}
