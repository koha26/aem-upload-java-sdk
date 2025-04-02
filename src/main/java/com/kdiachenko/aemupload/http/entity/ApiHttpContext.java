package com.kdiachenko.aemupload.http.entity;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
public class ApiHttpContext {
    @Builder.Default
    private Map<String, String> attributes = new LinkedHashMap<>();
}
