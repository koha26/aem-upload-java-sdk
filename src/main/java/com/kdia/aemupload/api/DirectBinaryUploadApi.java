package com.kdia.aemupload.api;

import com.kdia.aemupload.model.AssetApiResponse;
import com.kdia.aemupload.options.CompleteUploadRequestOptions;
import com.kdia.aemupload.options.CompleteUploadResponse;
import com.kdia.aemupload.options.InitiateUploadRequestOptions;
import com.kdia.aemupload.options.InitiateUploadResponse;
import com.kdia.aemupload.options.UploadBinaryRequestOptions;
import com.kdia.aemupload.options.UploadBinaryResponse;

public interface DirectBinaryUploadApi {
    AssetApiResponse<InitiateUploadResponse> initiateUpload(InitiateUploadRequestOptions request);

    AssetApiResponse<UploadBinaryResponse> uploadBinary(UploadBinaryRequestOptions request);

    AssetApiResponse<CompleteUploadResponse> completeUpload(CompleteUploadRequestOptions request);
}
