package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.options.CompleteBinaryUploadOptions;
import com.kdiachenko.aemupload.options.CompleteUploadResponse;
import com.kdiachenko.aemupload.options.InitiateBinaryUploadOptions;
import com.kdiachenko.aemupload.options.InitiateUploadResponse;
import com.kdiachenko.aemupload.options.UploadBinaryOptions;
import com.kdiachenko.aemupload.options.UploadBinaryResponse;

public interface DirectBinaryUploadApi {
    AssetApiResponse<InitiateUploadResponse> initiateUpload(InitiateBinaryUploadOptions request);

    AssetApiResponse<UploadBinaryResponse> uploadBinary(UploadBinaryOptions request);

    AssetApiResponse<CompleteUploadResponse> completeUpload(CompleteBinaryUploadOptions request);
}
