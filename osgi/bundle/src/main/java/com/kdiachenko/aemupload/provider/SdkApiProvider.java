package com.kdiachenko.aemupload.provider;

import com.kdia.aemupload.api.AssetFolderApi;
import com.kdia.aemupload.api.AssetMetadataApi;
import com.kdia.aemupload.api.DirectBinaryUploadApi;

/**
 * Interface for providing various AEM Upload SDK API instances.
 */
public interface SdkApiProvider {

    /**
     * Gets the Direct Binary Upload API instance.
     *
     * @return the Direct Binary Upload API instance
     */
    DirectBinaryUploadApi getDirectBinaryUploadApi();

    /**
     * Gets the Asset Folder API instance.
     *
     * @return the Asset Folder API instance
     */
    AssetFolderApi getAssetFolderApi();

    /**
     * Gets the Asset Metadata API instance.
     *
     * @return the Asset Metadata API instance
     */
    AssetMetadataApi getAssetMetadataApi();
}
