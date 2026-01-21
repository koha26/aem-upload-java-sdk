package com.kdiachenko.aemupload.internal.utils;

import com.kdiachenko.aemupload.utils.PathNormalizer;
import org.apache.commons.lang3.StringUtils;

/**
 * Default implementation of PathNormalizer.
 */
public class PathNormalizerImpl implements PathNormalizer {
    @Override
    public String normalize(final String path) {
        if (path == null) {
            return null;
        }
        String normalizedPath = StringUtils.removeStart(path, "/content/dam/");
        normalizedPath = StringUtils.removeStart(normalizedPath, "/");
        return "/api/assets/" + normalizedPath;
    }
}

