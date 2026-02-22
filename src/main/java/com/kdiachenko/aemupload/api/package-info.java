/**
 * Public API interfaces for the AEM Upload SDK.
 *
 * <p>This package contains the main API interfaces that consumers use to interact
 * with AEM Assets:</p>
 * <ul>
 *   <li>{@link com.kdiachenko.aemupload.api.DirectBinaryUploadApi} - Direct binary upload operations</li>
 *   <li>{@link com.kdiachenko.aemupload.api.AssetFolderApi} - DAM folder operations</li>
 *   <li>{@link com.kdiachenko.aemupload.api.AssetMetadataApi} - Asset metadata operations</li>
 * </ul>
 *
 * <p>API instances are obtained from {@link com.kdiachenko.aemupload.AemUploadSdk}:</p>
 * <pre>{@code
 * AemUploadSdk sdk = AemUploadSdk.builder()...build();
 * DirectBinaryUploadApi uploadApi = sdk.directBinaryUploadApi();
 * }</pre>
 *
 * @see com.kdiachenko.aemupload.AemUploadSdk
 */
package com.kdiachenko.aemupload.api;
