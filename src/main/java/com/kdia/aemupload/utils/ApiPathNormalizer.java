package com.kdia.aemupload.utils;

import org.apache.commons.lang3.StringUtils;

public final class ApiPathNormalizer {
    private ApiPathNormalizer() {
    }

    public static String normalize(String path) {
        if (path == null) {
            return null;
        }
        var normalizedPath = StringUtils.removeStart(path, "/content/dam");
        return "/api/assets/" + normalizedPath;
    }
}
