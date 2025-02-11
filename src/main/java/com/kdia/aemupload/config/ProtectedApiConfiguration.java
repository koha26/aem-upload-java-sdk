package com.kdia.aemupload.config;

import java.util.List;
import java.util.regex.Pattern;

public interface ProtectedApiConfiguration {
    List<Pattern> getProtectedApiUriPatterns();
}
