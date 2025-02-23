package com.kdia.aemupload.model;

import com.kdia.aemupload.http.entity.ApiHttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class AssetApiResponse<T> {
    private boolean success;
    private T body;
    private String errorMessage;

    public static <T> AssetApiResponse<T> success(T body) {
        return new AssetApiResponse<>(true, body, null);
    }

    public static <T> AssetApiResponse<T> fail(String errorMessage) {
        return new AssetApiResponse<>(false, null, errorMessage);
    }

    public static <T> AssetApiResponse<T> map(ApiHttpResponse<T> response) {
        if (response.isSuccess()) {
            return AssetApiResponse.success(response.getBody());
        }
        return AssetApiResponse.fail(response.getErrorMessage());
    }
}
