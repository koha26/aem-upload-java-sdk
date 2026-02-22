package com.kdiachenko.aemupload.provider;

import com.kdiachenko.aemupload.AemUploadSdk;
import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;

/**
 * OSGi service interface for accessing AEM Upload SDK APIs.
 *
 * <p>This service provides access to the SDK APIs as OSGi services. Inject this service
 * into your OSGi components to use the SDK:</p>
 *
 * <pre>{@code
 * @Reference
 * private AemUploadSdkService sdkService;
 *
 * public void uploadAsset() {
 *     DirectBinaryUploadApi api = sdkService.directBinaryUploadApi();
 *     // Use the API...
 * }
 * }</pre>
 *
 * <p>The service is configured via OSGi configuration (Felix console or .cfg files).</p>
 *
 * @see DirectBinaryUploadApi
 * @see AssetFolderApi
 * @see AssetMetadataApi
 */
public interface AemUploadSdkService {

    /**
     * Returns the Direct Binary Upload API.
     *
     * @return the DirectBinaryUploadApi instance
     * @throws IllegalStateException if the SDK is not properly configured
     */
    DirectBinaryUploadApi directBinaryUploadApi();

    /**
     * Returns the Asset Folder API.
     *
     * @return the AssetFolderApi instance
     * @throws IllegalStateException if the SDK is not properly configured
     */
    AssetFolderApi assetFolderApi();

    /**
     * Returns the Asset Metadata API.
     *
     * @return the AssetMetadataApi instance
     * @throws IllegalStateException if the SDK is not properly configured
     */
    AssetMetadataApi assetMetadataApi();

    /**
     * Checks if the SDK is properly configured and ready to use.
     *
     * @return true if the SDK is ready, false otherwise
     */
    boolean isReady();

    /**
     * Returns the underlying SDK instance.
     *
     * <p><strong>Note:</strong> The SDK lifecycle is managed by this service.
     * Do not close the returned SDK instance.</p>
     *
     * @return the AemUploadSdk instance
     * @throws IllegalStateException if the SDK is not properly configured
     */
    AemUploadSdk getSdk();
}
