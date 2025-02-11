package com.kdia.aemupload.model;

import com.kdia.aemupload.http.ApiHttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class AssetApiResponse<T> extends ApiHttpResponse<T> {
}
