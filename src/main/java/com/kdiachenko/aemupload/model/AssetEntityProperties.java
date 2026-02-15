package com.kdiachenko.aemupload.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Properties for an asset or folder entity in AEM.
 *
 * <p>This class captures common fields and a metadata map for arbitrary properties
 * returned by the Assets API.</p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetEntityProperties {
    private String hidden;
    private String name;
    private Map<String, Object> metadata = new HashMap<>();
}
