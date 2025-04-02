package com.kdiachenko.aemupload.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DamAsset {
    @JsonProperty("dam:assetState")
    private String assetState;
    @JsonProperty("dam:runDMProcess")
    private boolean runDMProcess;
    private DamAssetMetadata metadata;
}
