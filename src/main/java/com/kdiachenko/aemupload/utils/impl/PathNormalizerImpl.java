package com.kdiachenko.aemupload.utils.impl;

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
        String normalizedPath = "/content/dam".equals(path)
                ? "/" : StringUtils.removeStart(path, "/content/dam/");
        normalizedPath = StringUtils.removeStart(normalizedPath, "/");
        return "/api/assets/" + normalizedPath;
    }
}
