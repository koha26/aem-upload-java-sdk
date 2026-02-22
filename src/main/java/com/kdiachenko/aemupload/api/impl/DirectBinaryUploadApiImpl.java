package com.kdiachenko.aemupload.api.impl;

import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.exception.SdkError;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.entity.HttpContexts;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.options.CompleteBinaryUploadOptions;
import com.kdiachenko.aemupload.options.CompleteUploadResponse;
import com.kdiachenko.aemupload.options.InitiateBinaryUploadOptions;
import com.kdiachenko.aemupload.options.InitiateUploadResponse;
import com.kdiachenko.aemupload.options.UploadBinaryOptions;
import com.kdiachenko.aemupload.options.UploadBinaryResponse;
import com.kdiachenko.aemupload.utils.FileSplitter;
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

import static org.apache.hc.core5.http.ContentType.APPLICATION_FORM_URLENCODED;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@AllArgsConstructor
public class DirectBinaryUploadApiImpl implements DirectBinaryUploadApi {

    private final ApiHttpClient apiHttpClient;
    private final ApiServerConfiguration apiServerConfiguration;
    private final FileSplitter fileSplitter;

    @Override
    public AssetApiResponse<InitiateUploadResponse> initiateUpload(final InitiateBinaryUploadOptions request) {
        try {
            var initiateUploadUrl = buildInitiateUploadUrl(request);
            var httpEntity = ApiHttpEntity.builder()
                    .body(toInitiateUploadFormData(request))
                    .headers(Map.of(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString()))
                    .build();
            ApiHttpResponse<InitiateUploadResponse> responseEntity = apiHttpClient.post(
                    initiateUploadUrl, httpEntity, HttpContexts.AUTHORIZED, InitiateUploadResponse.class);

            return AssetApiResponse.map(responseEntity);
        } catch (Exception e) {
            log.error("Failed to initiate upload of {} to {}", request.getFileName(), request.getDamAssetFolder(), e);
            return AssetApiResponse.fail(SdkError.transportError("Failed to initiate upload", e));
        }
    }

    @Override
    public AssetApiResponse<UploadBinaryResponse> uploadBinary(final UploadBinaryOptions request) {
        List<Path> parts = List.of();
        try {
            var maxPartSize = request.getMaxPartSize();

            parts = fileSplitter.splitFile(request.getBinary(), maxPartSize);
            if (request.getUploadURIs().size() < parts.size()) {
                return AssetApiResponse.fail(SdkError.apiError(
                        "uploadURIs size (" + request.getUploadURIs().size() + ") does not match parts count ("
                                + parts.size() + ")", 400));
            }

            for (int i = 0; i < parts.size(); i++) {
                Path partPath = parts.get(i);
                URI uploadUri = request.getUploadURIs().get(i);
                ApiHttpResponse<Void> response;
                try (InputStream partInputStream = Files.newInputStream(partPath)) {
                    response = uploadPart(uploadUri, request.getContentType(), partInputStream);
                }
                if (!response.isSuccess()) {
                    return AssetApiResponse.fail(SdkError.apiError(
                            "Failed to upload binary part",
                            response.getStatus(),
                            response.getErrorMessage()
                    ));
                }
                log.info("Uploaded {} binary part to {}", i, uploadUri);
            }
            return AssetApiResponse.success(new UploadBinaryResponse(parts.size()));
        } catch (Exception e) {
            log.error("Failed to upload binary", e);
            return AssetApiResponse.fail(SdkError.transportError("Failed to upload binary", e));
        } finally {
            for (Path part : parts) {
                try {
                    Files.deleteIfExists(part);
                } catch (Exception e) {
                    log.warn("Failed to delete temp part {}", part, e);
                }
            }
        }
    }

    @Override
    public AssetApiResponse<CompleteUploadResponse> completeUpload(final CompleteBinaryUploadOptions request) {
        try {
            var httpEntity = ApiHttpEntity.builder()
                    .body(toCompleteUploadFormData(request))
                    .headers(Map.of(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString()))
                    .build();
            var completeUrl = apiServerConfiguration.getHostUrl() + request.getCompleteUri();
            ApiHttpResponse<CompleteUploadResponse> responseEntity =
                    apiHttpClient.post(completeUrl, httpEntity, HttpContexts.AUTHORIZED, CompleteUploadResponse.class);

            return AssetApiResponse.map(responseEntity);
        } catch (Exception e) {
            log.error("Failed to complete upload {}", request.getFileName(), e);
            return AssetApiResponse.fail(SdkError.transportError("Failed to complete upload", e));
        }
    }

    private ApiHttpResponse<Void> uploadPart(final URI uploadUrl, final String contentType,
                                             final InputStream partInputStream) {
        var decodedUri = decodeUploadBinaryPartUri(uploadUrl);
        var httpEntity = ApiHttpEntity.builder()
                .body(partInputStream)
                .headers(Map.of(CONTENT_TYPE, contentType))
                .build();
        ApiHttpResponse<Void> response = apiHttpClient.put(decodedUri, httpEntity, Void.class);
        if (!response.isSuccess()) {
            log.error("Failed to upload binary part to {}", uploadUrl);
        }
        return response;
    }

    private String decodeUploadBinaryPartUri(final URI uploadUrl) {
        return uploadUrl.toString();
        //return URLDecoder.decode(uploadUrl.toString(), StandardCharsets.UTF_8);
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
