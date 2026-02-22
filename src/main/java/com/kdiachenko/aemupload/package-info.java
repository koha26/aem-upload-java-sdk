/**
 * AEM Upload SDK - Public API.
 *
 * <p>This is the main entry point for the AEM Upload Java SDK. Use {@link com.kdiachenko.aemupload.AemUploadSdk}
 * to create and configure an SDK instance.</p>
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * // Create SDK with access token (for development)
 * try (AemUploadSdk sdk = AemUploadSdk.builder()
 *         .serverUrl("https://author.adobeaemcloud.com")
 *         .withAccessToken("your-dev-token")
 *         .build()) {
 *
 *     // Use the APIs
 *     var response = sdk.directBinaryUploadApi().initiateUpload(...);
 * }
 * }</pre>
 *
 * <h2>Package Structure</h2>
 * <ul>
 *   <li>{@code com.kdiachenko.aemupload} - Main SDK entry point</li>
 *   <li>{@code com.kdiachenko.aemupload.api} - Public API interfaces</li>
 *   <li>{@code com.kdiachenko.aemupload.config} - Configuration classes</li>
 *   <li>{@code com.kdiachenko.aemupload.model} - Data transfer objects</li>
 *   <li>{@code com.kdiachenko.aemupload.exception} - Exception types</li>
 *   <li>{@code com.kdiachenko.aemupload.internal} - Internal implementation (not for public use)</li>
 * </ul>
 *
 * @see com.kdiachenko.aemupload.AemUploadSdk
 */
package com.kdiachenko.aemupload;
