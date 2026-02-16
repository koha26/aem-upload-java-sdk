package com.kdiachenko.aemupload.utils;

/**
 * Interface for normalizing API paths.
 */
public interface PathNormalizer {
    /**
     * Normalizes a DAM asset path to the API format.
     *
     * @param path the path to normalize
     * @return the normalized path (e.g., "/api/assets/my-folder")
     */
    String normalize(String path);
}

