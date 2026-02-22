package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.AssetElement;

import java.util.Map;

/**
 * API for managing AEM Assets folders.
 *
 * <p>Provides basic folder retrieval and creation operations.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * AssetFolderApi api = sdk.assetFolderApi();
 * api.createFolder("/content/dam/my-folder").getOrThrow();
 * AssetElement folder = api.getFolder("/content/dam/my-folder").getOrThrow();
 * }</pre>
 */
public interface AssetFolderApi {
    /**
     * Retrieves a folder representation from AEM Assets.
     *
     * @param folder DAM folder path (e.g., "/content/dam/my-folder")
     * @return response containing folder details
     */
    AssetApiResponse<AssetElement> getFolder(String folder);

    /**
     * Creates a DAM folder with default properties.
     *
     * @param folder DAM folder path to create
     * @return response indicating success or failure
     */
    AssetApiResponse<Void> createFolder(String folder);

    /**
     * Creates a DAM folder with custom properties.
     *
     * @param folder DAM folder path to create
     * @param properties additional folder properties
     * @return response indicating success or failure
     */
    AssetApiResponse<Void> createFolder(String folder, Map<String, String> properties);
}
