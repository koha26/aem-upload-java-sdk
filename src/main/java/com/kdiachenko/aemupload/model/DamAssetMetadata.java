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
public class DamAssetMetadata {
    @JsonProperty("dam:scene7Domain")
    private String scene7Domain;
    @JsonProperty("dam:scene7File")
    private String scene7File;
    @JsonProperty("dam:scene7FileStatus")
    private String scene7FileStatus;
    @JsonProperty("dam:scene7Type")
    private String scene7Type;
    @JsonProperty("dc:format")
    private String format;
    @JsonProperty("dam:size")
    private long size;
}
