package com.kdiachenko.aemupload.provider.impl;

import com.kdiachenko.aemupload.AemUploadSdk;
import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.config.ServiceCredentialsAuthConfig;
import com.kdiachenko.aemupload.provider.AemUploadSdkService;
import com.kdiachenko.aemupload.provider.SdkApiProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Component(
        service = {AemUploadSdkService.class, SdkApiProvider.class},
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        immediate = true
)
@Designate(ocd = AemUploadSdkServiceImpl.Config.class)
public class AemUploadSdkServiceImpl implements AemUploadSdkService, SdkApiProvider {

    private static final Logger log = LoggerFactory.getLogger(AemUploadSdkServiceImpl.class);

    private volatile AemUploadSdk sdk;
    private volatile boolean ready = false;

    @Activate
    @Modified
    protected void activate(Config config) {
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

    private AemUploadSdk buildSdk(Config config) {
        var builder = AemUploadSdk.builder()
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

    private ServiceCredentialsAuthConfig buildServiceCredentials(Config config) {
        var builder = ServiceCredentialsAuthConfig.builder()
                .clientId(config.clientId())
                .clientSecret(config.clientSecret())
                .technicalAccountId(config.technicalAccountId())
                .orgId(config.orgId());

        // Private key - either content or file path
        String privateKeyContent = config.privateKeyContent();
        String privateKeyPath = config.privateKeyPath();

        if (privateKeyContent != null && !privateKeyContent.isBlank()) {
            builder.privateKeyContent(privateKeyContent);
        } else if (privateKeyPath != null && !privateKeyPath.isBlank()) {
            builder.privateKeyFilePath(privateKeyPath);
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

    // ========== AemUploadSdkService implementation ==========

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

    // ========== SdkApiProvider implementation (backward compatibility) ==========

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

    // ========== OSGi Configuration ==========

    @ObjectClassDefinition(
            name = "AEM Upload SDK Configuration",
            description = "Configuration for the AEM Upload SDK OSGi service"
    )
    public @interface Config {

        @AttributeDefinition(
                name = "Server URL",
                description = "The AEM server URL (e.g., https://author.adobeaemcloud.com or http://localhost:4502)"
        )
        String serverUrl() default "http://localhost:4502";

        @AttributeDefinition(
                name = "Authentication Type",
                description = "The authentication method to use",
                options = {
                        @Option(label = "Access Token", value = "accessToken"),
                        @Option(label = "Basic Auth (username/password)", value = "basic"),
                        @Option(label = "Service Credentials (JWT)", value = "serviceCredentials")
                }
        )
        String authType() default "basic";

        // ===== Access Token Auth =====

        @AttributeDefinition(
                name = "Access Token",
                description = "Static access token (for development). Used when authType = 'accessToken'"
        )
        String accessToken() default "";

        // ===== Basic Auth =====

        @AttributeDefinition(
                name = "Username",
                description = "Username for basic authentication. Used when authType = 'basic'"
        )
        String username() default "admin";

        @AttributeDefinition(
                name = "Password",
                description = "Password for basic authentication. Used when authType = 'basic'"
        )
        String password() default "admin";

        // ===== Service Credentials (JWT) =====

        @AttributeDefinition(
                name = "Client ID",
                description = "Adobe I/O client ID. Used when authType = 'serviceCredentials'"
        )
        String clientId() default "";

        @AttributeDefinition(
                name = "Client Secret",
                description = "Adobe I/O client secret. Used when authType = 'serviceCredentials'"
        )
        String clientSecret() default "";

        @AttributeDefinition(
                name = "Technical Account ID",
                description = "Adobe I/O technical account ID. Used when authType = 'serviceCredentials'"
        )
        String technicalAccountId() default "";

        @AttributeDefinition(
                name = "Organization ID",
                description = "Adobe organization ID (e.g., XXXXX@AdobeOrg). Used when authType = 'serviceCredentials'"
        )
        String orgId() default "";

        @AttributeDefinition(
                name = "Private Key Content",
                description = "PEM-encoded private key content. Used when authType = 'serviceCredentials'. " +
                        "Either this or privateKeyPath is required."
        )
        String privateKeyContent() default "";

        @AttributeDefinition(
                name = "Private Key Path",
                description = "Path to the private key file. Used when authType = 'serviceCredentials'. " +
                        "Either this or privateKeyContent is required."
        )
        String privateKeyPath() default "";

        @AttributeDefinition(
                name = "Meta Scopes",
                description = "Adobe I/O meta scopes. Used when authType = 'serviceCredentials'"
        )
        String[] metaScopes() default {"ent_aem_cloud_api"};

        @AttributeDefinition(
                name = "IMS Endpoint",
                description = "Adobe IMS endpoint. Used when authType = 'serviceCredentials'"
        )
        String imsEndpoint() default "https://ims-na1.adobelogin.com/ims/exchange/jwt";
    }
}
