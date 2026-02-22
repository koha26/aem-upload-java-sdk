package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.options.CompleteBinaryUploadOptions;
import com.kdiachenko.aemupload.options.CompleteUploadResponse;
import com.kdiachenko.aemupload.options.InitiateBinaryUploadOptions;
import com.kdiachenko.aemupload.options.InitiateUploadResponse;
import com.kdiachenko.aemupload.options.UploadBinaryOptions;
import com.kdiachenko.aemupload.options.UploadBinaryResponse;

/**
 * API for the direct binary upload flow in AEM.
 *
 * <p>This API models the three-step process:</p>
 * <ol>
 *   <li>Initiate upload to receive upload URIs and token</li>
 *   <li>Upload binary parts to the provided URIs</li>
 *   <li>Complete the upload to finalize the asset</li>
 * </ol>
 *
 * <p>Example:</p>
 * <pre>{@code
 * DirectBinaryUploadApi api = sdk.directBinaryUploadApi();
 * InitiateUploadResponse init = api.initiateUpload(
 *     InitiateBinaryUploadOptions.builder()
 *         .damAssetFolder("/content/dam/my-folder")
 *         .fileName("image.jpg")
 *         .fileSize(1024)
 *         .build()
 * ).getOrThrow();
 *
 * UploadBinaryResponse upload = api.uploadBinary(
 *     UploadBinaryOptions.builder()
 *         .binary(Paths.get("/tmp/image.jpg"))
 *         .uploadURIs(init.getFiles().get(0).getUploadURIs())
 *         .maxPartSize(init.getFiles().get(0).getMaxPartSize())
 *         .contentType("image/jpeg")
 *         .build()
 * ).getOrThrow();
 *
 * CompleteUploadResponse complete = api.completeUpload(
 *     CompleteBinaryUploadOptions.builder()
 *         .completeUri(init.getCompleteURI())
 *         .fileName("image.jpg")
 *         .mimeType("image/jpeg")
 *         .uploadToken(init.getFiles().get(0).getUploadToken())
 *         .build()
 * ).getOrThrow();
 * }</pre>
 */
public interface DirectBinaryUploadApi {
    /**
     * Initiates a direct binary upload to obtain upload URIs and tokens.
     *
     * @param request initiation options (file name, size, folder)
     * @return response containing upload URIs and tokens
     */
    AssetApiResponse<InitiateUploadResponse> initiateUpload(InitiateBinaryUploadOptions request);

    /**
     * Uploads the binary content to the pre-signed URIs returned by the initiate step.
     *
     * @param request options including the local file and upload URIs
     * @return response with upload result details
     */
    AssetApiResponse<UploadBinaryResponse> uploadBinary(UploadBinaryOptions request);

    /**
     * Completes the direct binary upload by notifying AEM to finalize the asset.
     *
     * @param request options for completing the upload
     * @return response containing final asset information
     */
    AssetApiResponse<CompleteUploadResponse> completeUpload(CompleteBinaryUploadOptions request);
}
