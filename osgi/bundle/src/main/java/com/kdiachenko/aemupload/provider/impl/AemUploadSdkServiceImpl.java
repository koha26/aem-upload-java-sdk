package com.kdiachenko.aemupload.provider.impl;

import com.kdiachenko.aemupload.AemUploadSdk;
import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.config.ServiceCredentialsAuthConfig;
import com.kdiachenko.aemupload.http.HttpClient5BuilderFactory;
import com.kdiachenko.aemupload.provider.AemUploadSdkService;
import com.kdiachenko.aemupload.provider.AemUploadSdkServiceConfig;
import com.kdiachenko.aemupload.provider.SdkApiProvider;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.annotations.Designate;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

/**
 * OSGi service implementation that provides access to the AEM Upload SDK.
 *
 * <p>This implementation uses the new {@link AemUploadSdk#builder()} pattern and
 * properly manages the SDK lifecycle (creation on activate, cleanup on deactivate).</p>
 *
 * <p>Configuration is done via OSGi configuration with support for multiple
 * authentication types:</p>
 * <ul>
 *   <li><strong>accessToken</strong> - Use a static access token (for development)</li>
 *   <li><strong>basic</strong> - Use username/password basic authentication</li>
 *   <li><strong>serviceCredentials</strong> - Use JWT service credentials (for production)</li>
 * </ul>
 *
 * @see AemUploadSdkService
 * @see SdkApiProvider
 */
@Slf4j
@Component(
        service = {AemUploadSdkService.class, SdkApiProvider.class},
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        immediate = true
)
@Designate(ocd = AemUploadSdkServiceConfig.class)
public class AemUploadSdkServiceImpl implements AemUploadSdkService, SdkApiProvider {

    private final HttpClient5BuilderFactory httpClient5BuilderFactory;
    private volatile AemUploadSdk sdk;
    private volatile boolean ready = false;

    @Activate
    public AemUploadSdkServiceImpl(@Reference(cardinality = ReferenceCardinality.OPTIONAL)
                                   HttpClient5BuilderFactory httpClient5BuilderFactory) {
        this.httpClient5BuilderFactory = httpClient5BuilderFactory;
    }

    @Activate
    @Modified
    protected void activate(AemUploadSdkServiceConfig config) {
        log.info("Activating AEM Upload SDK Service with server URL: {}", config.serverUrl());

        // Close existing SDK if reconfiguring
        closeExistingSdk();

        try {
            sdk = buildSdk(config);
            ready = true;
            log.info("AEM Upload SDK Service activated successfully");
        } catch (Exception e) {
            log.error("Failed to initialize AEM Upload SDK", e);
            ready = false;
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Deactivating AEM Upload SDK Service");
        closeExistingSdk();
        ready = false;
    }

    private void closeExistingSdk() {
        if (sdk != null) {
            closeQuietly(sdk);
            sdk = null;
        }
    }

    void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            log.warn("Error closing AEM Upload SDK", e);
        }
    }

    private AemUploadSdk buildSdk(AemUploadSdkServiceConfig config) {
        var builder = AemUploadSdk.builder()
                .httpClientBuilderFactory(httpClient5BuilderFactory)
                .serverUrl(config.serverUrl());

        String authType = config.authType();
        switch (authType) {
            case "accessToken":
                String token = config.accessToken();
                if (token == null || token.isBlank()) {
                    throw new IllegalStateException("Access token is required for 'accessToken' auth type");
                }
                builder.withAccessToken(token);
                break;

            case "basic":
                builder.withBasicAuth(config.username(), config.password());
                break;

            case "serviceCredentials":
                builder.withServiceCredentials(buildServiceCredentials(config));
                break;

            default:
                throw new IllegalStateException("Unknown auth type: " + authType);
        }

        return builder.build();
    }

    private ServiceCredentialsAuthConfig buildServiceCredentials(AemUploadSdkServiceConfig config) {
        var builder = ServiceCredentialsAuthConfig.builder()
                .clientId(config.clientId())
                .clientSecret(config.clientSecret())
                .technicalAccountId(config.technicalAccountId())
                .orgId(config.orgId());

        // Private key - either content or file path
        String privateKeyContent = config.privateKeyContent();

        if (privateKeyContent != null && !privateKeyContent.isBlank()) {
            builder.privateKeyContent(privateKeyContent);
        } else {
            throw new IllegalStateException("Either privateKeyContent or privateKeyPath is required for service credentials");
        }

        String[] metaScopes = config.metaScopes();
        if (metaScopes != null && metaScopes.length > 0) {
            builder.metaScopes(Arrays.asList(metaScopes));
        }

        String imsEndpoint = config.imsEndpoint();
        if (imsEndpoint != null && !imsEndpoint.isBlank()) {
            builder.imsEndpoint(imsEndpoint);
        }

        return builder.build();
    }

    private void ensureReady() {
        if (!ready || sdk == null) {
            throw new IllegalStateException("AEM Upload SDK is not properly configured or not ready");
        }
    }

    @Override
    public DirectBinaryUploadApi directBinaryUploadApi() {
        ensureReady();
        return sdk.directBinaryUploadApi();
    }

    @Override
    public AssetFolderApi assetFolderApi() {
        ensureReady();
        return sdk.assetFolderApi();
    }

    @Override
    public AssetMetadataApi assetMetadataApi() {
        ensureReady();
        return sdk.assetMetadataApi();
    }

    @Override
    public boolean isReady() {
        return ready && sdk != null;
    }

    @Override
    public AemUploadSdk getSdk() {
        ensureReady();
        return sdk;
    }

    @Override
    public DirectBinaryUploadApi getDirectBinaryUploadApi() {
        return directBinaryUploadApi();
    }

    @Override
    public AssetFolderApi getAssetFolderApi() {
        return assetFolderApi();
    }

    @Override
    public AssetMetadataApi getAssetMetadataApi() {
        return assetMetadataApi();
    }

}
