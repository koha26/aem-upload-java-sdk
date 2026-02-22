package com.kdiachenko.aemupload.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents DAM asset metadata returned by the AEM Assets API.
 *
 * <p>This model mirrors the JSON structure returned by endpoints like
 * {@code /api/assets/...} and is used by {@link com.kdiachenko.aemupload.api.AssetMetadataApi}.</p>
 */
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
