/**
 * HTTP client abstraction layer.
 *
 * <p>This package provides HTTP client abstractions that can be customized or replaced:</p>
 * <ul>
 *   <li>{@link com.kdiachenko.aemupload.http.HttpClient5BuilderFactory} - Factory for creating HTTP client builders</li>
 *   <li>{@link com.kdiachenko.aemupload.http.HttpClient5BuilderConfigurator} - Configures HTTP client builders</li>
 * </ul>
 *
 * <p>Sub-packages:</p>
 * <ul>
 *   <li>{@code client} - HTTP client interfaces and builders</li>
 *   <li>{@code entity} - HTTP request/response entities</li>
 *   <li>{@code response} - Response handlers</li>
 * </ul>
 *
 * @see com.kdiachenko.aemupload.AemUploadSdk.Builder#httpClient(org.apache.hc.client5.http.impl.classic.CloseableHttpClient)
 */
package com.kdiachenko.aemupload.http;
