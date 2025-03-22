package com.kdia.aemupload.api.impl;

import com.kdia.aemupload.api.DirectBinaryUploadApi;
import com.kdia.aemupload.config.ApiServerConfiguration;
import com.kdia.aemupload.http.client.ApiHttpClient;
import com.kdia.aemupload.http.entity.ApiHttpEntity;
import com.kdia.aemupload.http.entity.ApiHttpResponse;
import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.options.CompleteBinaryUploadOptions;
import com.kdia.aemupload.options.CompleteUploadResponse;
import com.kdia.aemupload.options.InitiateBinaryUploadOptions;
import com.kdia.aemupload.options.InitiateUploadResponse;
import com.kdia.aemupload.options.UploadBinaryOptions;
import com.kdia.aemupload.options.UploadBinaryResponse;
import com.kdia.aemupload.utils.FileSplitUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.kdia.aemupload.http.client.ApiHttpClient.AUTHORIZABLE_API_REQUEST;
import static org.apache.hc.core5.http.ContentType.APPLICATION_FORM_URLENCODED;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@AllArgsConstructor
public class DirectBinaryUploadApiImpl implements DirectBinaryUploadApi {

    private final ApiHttpClient apiHttpClient;
    private final ApiServerConfiguration apiServerConfiguration;

    @Override
    public AssetApiResponse<InitiateUploadResponse> initiateUpload(final InitiateBinaryUploadOptions request) {
        try {
            var initiateUploadUrl = buildInitiateUploadUrl(request);
            var httpEntity = ApiHttpEntity.builder()
                    .body(toInitiateUploadFormData(request))
                    .headers(Map.of(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString()))
                    .build();
            ApiHttpResponse<InitiateUploadResponse> responseEntity =
                    apiHttpClient.post(initiateUploadUrl, httpEntity, AUTHORIZABLE_API_REQUEST, InitiateUploadResponse.class);

            return AssetApiResponse.map(responseEntity);
        } catch (Exception e) {
            log.error("Failed to initiate upload of {} to {}", request.getFileName(), request.getDamAssetFolder(), e);
            return AssetApiResponse.fail(e.getMessage());
        }
    }

    @Override
    public AssetApiResponse<UploadBinaryResponse> uploadBinary(final UploadBinaryOptions request) {
        try {
            var maxPartSize = request.getMaxPartSize();

            List<Path> parts = FileSplitUtil.splitFile(request.getBinary(), maxPartSize);

            for (int i = 0; i < parts.size(); i++) {
                var partInputStream = Files.newInputStream(parts.get(i));
                URI uploadUri = request.getUploadURIs().get(i);
                boolean isUploaded = uploadPart(uploadUri, request.getContentType(), partInputStream);
                Files.delete(parts.get(i));
                if (!isUploaded) {
                    return AssetApiResponse.fail("Failed to upload binary");
                }
                log.info("Uploaded {} binary part to {}", i, uploadUri);
            }
            return AssetApiResponse.success(new UploadBinaryResponse(parts.size()));
        } catch (Exception e) {
            log.error("Failed to upload binary", e);
            return AssetApiResponse.fail(e.getMessage());
        }
    }

    @Override
    public AssetApiResponse<CompleteUploadResponse> completeUpload(final CompleteBinaryUploadOptions request) {
        try {
            var httpEntity = ApiHttpEntity.builder()
                    .body(toCompleteUploadFormData(request))
                    .headers(Map.of(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString()))
                    .build();
            ApiHttpResponse<CompleteUploadResponse> responseEntity =
                    apiHttpClient.post(request.getCompleteUri(), httpEntity, AUTHORIZABLE_API_REQUEST, CompleteUploadResponse.class);

            return AssetApiResponse.map(responseEntity);
        } catch (Exception e) {
            log.error("Failed to complete upload {}", request.getFileName(), e);
            return AssetApiResponse.fail(e.getMessage());
        }
    }

    private boolean uploadPart(final URI uploadUrl, final String contentType, final InputStream partInputStream) {
        var decodedUri = decodeUploadBinaryPartUri(uploadUrl);
        var httpEntity = ApiHttpEntity.builder()
                .body(partInputStream)
                .headers(Map.of(CONTENT_TYPE, contentType))
                .build();
        ApiHttpResponse<Void> response = apiHttpClient.put(decodedUri, httpEntity, Void.class);
        if (!response.isSuccess()) {
            log.error("Failed to upload binary part to {}", uploadUrl);
        }
        return response.isSuccess();
    }

    private String decodeUploadBinaryPartUri(final URI uploadUrl) {
        return URLDecoder.decode(uploadUrl.toString(), StandardCharsets.UTF_8);
    }

    private Map<String, String> toCompleteUploadFormData(final CompleteBinaryUploadOptions request) {
        var formParams = new LinkedHashMap<String, String>();
        formParams.put("fileName", request.getFileName());
        formParams.put("mimeType", request.getMimeType());
        formParams.put("uploadToken", request.getUploadToken());
        formParams.put("createVersion", String.valueOf(request.isCreateVersion()));
        formParams.put("replace", String.valueOf(request.isReplace()));
        addIfPresent(formParams, "versionComment", request::getVersionComment);
        addIfPresent(formParams, "uploadDuration", request::getUploadDuration);
        addIfPresent(formParams, "fileSize", request::getFileSize);
        return formParams;
    }

    private void addIfPresent(final Map<String, String> formParams,
                              final String key, final Supplier<Object> valueSupplier) {
        Optional.ofNullable(valueSupplier.get())
                .ifPresent(value -> formParams.put(key, String.valueOf(value)));
    }

    private Map<String, Object> toInitiateUploadFormData(final InitiateBinaryUploadOptions options) {
        return Map.of(
                "fileName", options.getFileName(),
                "fileSize", options.getFileSize()
        );
    }

    private String buildInitiateUploadUrl(final InitiateBinaryUploadOptions options) {
        var normalizedDamAssetFolder = StringUtils.removeEnd(options.getDamAssetFolder(), "/");
        return apiServerConfiguration.getHostUrl() + normalizedDamAssetFolder + ".initiateUpload.json";
    }
}
