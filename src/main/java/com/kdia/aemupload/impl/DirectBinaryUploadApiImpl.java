package com.kdia.aemupload.impl;

import com.kdia.aemupload.api.DirectBinaryUploadApi;
import com.kdia.aemupload.expection.ApiHttpClientException;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.ApiHttpResponse;
import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.options.CompleteUploadRequestOptions;
import com.kdia.aemupload.options.CompleteUploadResponse;
import com.kdia.aemupload.options.InitiateUploadRequestOptions;
import com.kdia.aemupload.options.InitiateUploadResponse;
import com.kdia.aemupload.options.UploadBinaryRequestOptions;
import com.kdia.aemupload.options.UploadBinaryResponse;
import com.kdia.aemupload.utils.FileSplitUtil;
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

@Slf4j
public class DirectBinaryUploadApiImpl implements DirectBinaryUploadApi {

    private final ApiHttpClient apiHttpClient;

    public DirectBinaryUploadApiImpl(ApiHttpClient apiHttpClient) {
        this.apiHttpClient = apiHttpClient;
    }

    @Override
    public AssetApiResponse<InitiateUploadResponse> initiateUpload(final InitiateUploadRequestOptions request) {
        try {
            var formData = toInitiateUploadFormData(request);
            var initiateUploadUrl = buildInitiateUploadUrl(request);
            var headers = Map.of("Content-Type", "application/x-www-form-urlencoded");
            ApiHttpResponse<InitiateUploadResponse> responseEntity =
                    apiHttpClient.post(initiateUploadUrl, formData, headers, InitiateUploadResponse.class);

            return AssetApiResponse.success(responseEntity.getBody());
        } catch (ApiHttpClientException e) {
            log.error("Failed to initiate upload of {} to {}", request.getFileName(), request.getDamAssetFolder(), e);
            return AssetApiResponse.fail(e.getErrorMessage());
        }
    }

    @Override
    public AssetApiResponse<UploadBinaryResponse> uploadBinary(final UploadBinaryRequestOptions request) {
        try {
            var maxPartSize = request.getMaxPartSize();

            List<Path> parts = FileSplitUtil.splitFile(request.getBinary(), maxPartSize);

            for (int i = 0; i < parts.size(); i++) {
                InputStream partInputStream = Files.newInputStream(parts.get(i));
                URI uploadUri = request.getUploadURIs().get(i);
                uploadPart(uploadUri, request.getContentType(), partInputStream);
                Files.delete(parts.get(i));
                log.info("Uploaded {} binary part to {}", i, uploadUri);
            }
            return AssetApiResponse.success(new UploadBinaryResponse(parts.size()));
        } catch (ApiHttpClientException e) {
            log.error("Failed to upload binary", e);
            return AssetApiResponse.fail(e.getErrorMessage());
        } catch (Exception e) {
            log.error("Failed to handle binary part", e);
            return AssetApiResponse.fail("Failed to handle binary part");
        }
    }

    @Override
    public AssetApiResponse<CompleteUploadResponse> completeUpload(final CompleteUploadRequestOptions request) {
        try {
            var formData = toCompleteUploadFormData(request);
            var headers = Map.of("Content-Type", "application/x-www-form-urlencoded");
            ApiHttpResponse<CompleteUploadResponse> responseEntity =
                    apiHttpClient.post(request.getCompleteUri(), formData, headers, CompleteUploadResponse.class);

            return AssetApiResponse.success(responseEntity.getBody());
        } catch (ApiHttpClientException e) {
            log.error("Failed to complete upload {}", request.getFileName(), e);
            return AssetApiResponse.fail(e.getErrorMessage());
        }
    }

    private void uploadPart(final URI uploadUrl, final String contentType, final InputStream partInputStream) {
        var headers = Map.of("Content-Type", contentType);
        var decodedUri = decodeUploadBinaryPartUri(uploadUrl);
        apiHttpClient.put(decodedUri, partInputStream, headers, Void.class);
    }

    private String decodeUploadBinaryPartUri(URI uploadUrl) {
        return URLDecoder.decode(uploadUrl.toString(), StandardCharsets.UTF_8);
    }

    private Map<String, String> toCompleteUploadFormData(final CompleteUploadRequestOptions request) {
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

    private void addIfPresent(Map<String, String> formParams, String key, Supplier<Object> valueSupplier) {
        Optional.ofNullable(valueSupplier.get())
                .ifPresent(value -> formParams.put(key, String.valueOf(value)));
    }

    private Map<String, String> toInitiateUploadFormData(final InitiateUploadRequestOptions options) {
        return Map.of(
                "fileName", options.getFileName(),
                "fileSize", String.valueOf(options.getFileSize())
        );
    }

    private String buildInitiateUploadUrl(final InitiateUploadRequestOptions options) {
        var normalizedDamAssetFolder = StringUtils.removeEnd(options.getDamAssetFolder(), "/");
        return normalizedDamAssetFolder + ".initiateUpload.json";
    }
}
