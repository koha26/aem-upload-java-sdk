package com.kdiachenko.aemupload.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetEntityProperties {
    private String hidden;
    private String name;
    private Map<String, Object> metadata = new HashMap<>();
}

