package com.kdia.aemupload.api;

import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.options.CompleteBinaryUploadOptions;
import com.kdia.aemupload.options.CompleteUploadResponse;
import com.kdia.aemupload.options.InitiateBinaryUploadOptions;
import com.kdia.aemupload.options.InitiateUploadResponse;
import com.kdia.aemupload.options.UploadBinaryOptions;
import com.kdia.aemupload.options.UploadBinaryResponse;

public interface DirectBinaryUploadApi {
    AssetApiResponse<InitiateUploadResponse> initiateUpload(InitiateBinaryUploadOptions request);

    AssetApiResponse<UploadBinaryResponse> uploadBinary(UploadBinaryOptions request);

    AssetApiResponse<CompleteUploadResponse> completeUpload(CompleteBinaryUploadOptions request);
}
