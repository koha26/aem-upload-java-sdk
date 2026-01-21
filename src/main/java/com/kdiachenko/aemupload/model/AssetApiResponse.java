package com.kdiachenko.aemupload.model;

import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

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
        if (response == null) {
            return AssetApiResponse.fail("No response received from server");
        }
        if (response.isSuccess()) {
            return AssetApiResponse.success(response.getBody());
        }
        String message = StringUtils.isNotBlank(response.getErrorMessage())
                ? response.getErrorMessage()
                : "Request failed with status " + response.getStatus();
        return AssetApiResponse.fail(message);
    }
}
