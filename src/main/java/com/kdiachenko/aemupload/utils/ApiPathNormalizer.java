package com.kdiachenko.aemupload.utils;

import org.apache.commons.lang3.StringUtils;

public final class ApiPathNormalizer {
    private ApiPathNormalizer() {
    }

    public static String normalize(final String path) {
        if (path == null) {
            return null;
        }
        String normalizedPath = StringUtils.removeStart(path, "/content/dam/");
        normalizedPath = StringUtils.removeStart(normalizedPath, "/");
        return "/api/assets/" + normalizedPath;
    }
}
