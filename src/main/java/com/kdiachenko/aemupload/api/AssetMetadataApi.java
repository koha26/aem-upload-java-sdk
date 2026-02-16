package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.DamAsset;

import java.util.Map;

/**
 * API for reading and mutating asset metadata in AEM.
 *
 * <p>Example:</p>
 * <pre>{@code
 * AssetMetadataApi api = sdk.assetMetadataApi();
 * DamAsset asset = api.getAssetMetadata("/content/dam/my-folder/image.jpg").getOrThrow();
 * api.updateAssetMetadata("/content/dam/my-folder/image.jpg", Map.of("dc:title", "New Title")).getOrThrow();
 * }</pre>
 */
public interface AssetMetadataApi {
    /**
     * Retrieves asset metadata from AEM.
     *
     * @param assetPath DAM asset path
     * @return response containing asset metadata
     */
    AssetApiResponse<DamAsset> getAssetMetadata(String assetPath);

    /**
     * Updates asset metadata fields.
     *
     * @param assetPath DAM asset path
     * @param metadata metadata key/value pairs to update
     * @return response indicating success or failure
     */
    AssetApiResponse<Void> updateAssetMetadata(String assetPath, Map<String, String> metadata);

    /**
     * Deletes an asset in AEM.
     *
     * @param assetPath DAM asset path
     * @return response indicating success or failure
     */
    AssetApiResponse<Void> deleteAsset(String assetPath);
}
