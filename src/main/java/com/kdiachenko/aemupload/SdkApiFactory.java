package com.kdiachenko.aemupload;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;

/**
 * Factory interface for creating SDK API instances.
 *
 * @deprecated Use {@link AemUploadSdk#builder()} instead for a simpler and more fluent API.
 *             This interface will be removed in a future major version.
 *             <pre>{@code
 *             // Instead of:
 *             SdkApiFactory factory = new DefaultSdkApiFactory(...);
 *             DirectBinaryUploadApi api = factory.createDirectBinaryUploadApi();
 *
 *             // Use:
 *             AemUploadSdk sdk = AemUploadSdk.builder()
 *                 .serverUrl("https://...")
 *                 .withAccessToken("token")
 *                 .build();
 *             DirectBinaryUploadApi api = sdk.directBinaryUploadApi();
 *             }</pre>
 */
@Deprecated(forRemoval = true)
public interface SdkApiFactory {

    DirectBinaryUploadApi createDirectBinaryUploadApi();

    AssetFolderApi createAssetFolderApi();

    AssetMetadataApi createAssetMetadataApi();
}
