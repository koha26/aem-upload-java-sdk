package com.kdiachenko.aemupload;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.api.AssetFolderApiBuilder;
import com.kdiachenko.aemupload.api.AssetMetadataApiBuilder;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApiBuilder;
import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.auth.impl.ApiAuthorizationInterceptorImpl;
import com.kdiachenko.aemupload.auth.impl.ServiceCredentialsApiAccessTokenProvider;
import com.kdiachenko.aemupload.config.AccessTokenAuthConfig;
import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.config.AuthConfig;
import com.kdiachenko.aemupload.config.BasicAuthConfig;
import com.kdiachenko.aemupload.config.ServerConfig;
import com.kdiachenko.aemupload.config.ServiceCredentialsAuthConfig;
import com.kdiachenko.aemupload.http.HttpClient5BuilderFactory;
import com.kdiachenko.aemupload.http.HttpClient5BuilderConfigurator;
import com.kdiachenko.aemupload.utils.FileSplitter;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import com.kdiachenko.aemupload.internal.utils.FileSplitterImpl;
import com.kdiachenko.aemupload.internal.utils.PathNormalizerImpl;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Main entry point for the AEM Upload SDK.
 * Use {@link #builder()} to create and configure an SDK instance.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Simple usage with access token
 * AemUploadSdk sdk = AemUploadSdk.builder()
 *     .serverUrl("https://author.adobeaemcloud.com")
 *     .withAccessToken("your-dev-token")
 *     .build();
 *
 * // Upload a file
 * var initiateResponse = sdk.directBinaryUploadApi()
 *     .initiateUpload(InitiateBinaryUploadOptions.builder()
 *         .damAssetFolder("/content/dam/my-folder")
 *         .fileName("image.jpg")
 *         .fileSize(1024)
 *         .build());
 *
 * // Don't forget to close when done
 * sdk.close();
 * }</pre>
 *
 * <p>The SDK implements {@link Closeable} to properly clean up HTTP connections.
 * Consider using try-with-resources:</p>
 * <pre>{@code
 * try (AemUploadSdk sdk = AemUploadSdk.builder()...build()) {
 *     // Use the SDK
 * }
 * }</pre>
 */
public final class AemUploadSdk implements Closeable {

    private final ApiServerConfiguration serverConfig;
    private final CloseableHttpClient httpClient;
    private final FileSplitter fileSplitter;
    private final PathNormalizer pathNormalizer;
    private final boolean ownedHttpClient;

    // Lazily initialized API instances
    private volatile DirectBinaryUploadApi directBinaryUploadApi;
    private volatile AssetFolderApi assetFolderApi;
    private volatile AssetMetadataApi assetMetadataApi;

    private AemUploadSdk(Builder builder) {
        this.serverConfig = builder.serverConfig;
        this.httpClient = builder.httpClient;
        this.fileSplitter = builder.fileSplitter;
        this.pathNormalizer = builder.pathNormalizer;
        this.ownedHttpClient = builder.ownedHttpClient;
    }

    /**
     * Creates a new SDK builder.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the Direct Binary Upload API for uploading asset binaries.
     *
     * @return the DirectBinaryUploadApi instance
     */
    public DirectBinaryUploadApi directBinaryUploadApi() {
        if (directBinaryUploadApi == null) {
            synchronized (this) {
                if (directBinaryUploadApi == null) {
                    directBinaryUploadApi = DirectBinaryUploadApiBuilder.builder(serverConfig)
                            .withHttpClient(httpClient)
                            .withFileSplitter(fileSplitter)
                            .build();
                }
            }
        }
        return directBinaryUploadApi;
    }

    /**
     * Returns the Asset Folder API for managing DAM folders.
     *
     * @return the AssetFolderApi instance
     */
    public AssetFolderApi assetFolderApi() {
        if (assetFolderApi == null) {
            synchronized (this) {
                if (assetFolderApi == null) {
                    assetFolderApi = AssetFolderApiBuilder.builder(serverConfig)
                            .withHttpClient(httpClient)
                            .withPathNormalizer(pathNormalizer)
                            .build();
                }
            }
        }
        return assetFolderApi;
    }

    /**
     * Returns the Asset Metadata API for managing asset metadata.
     *
     * @return the AssetMetadataApi instance
     */
    public AssetMetadataApi assetMetadataApi() {
        if (assetMetadataApi == null) {
            synchronized (this) {
                if (assetMetadataApi == null) {
                    assetMetadataApi = AssetMetadataApiBuilder.builder(serverConfig)
                            .withHttpClient(httpClient)
                            .withPathNormalizer(pathNormalizer)
                            .build();
                }
            }
        }
        return assetMetadataApi;
    }

    /**
     * Returns the server configuration.
     *
     * @return the server configuration
     */
    public ApiServerConfiguration getServerConfig() {
        return serverConfig;
    }

    /**
     * Closes the SDK and releases any resources.
     * If the HTTP client was created by the SDK, it will be closed.
     * If a custom HTTP client was provided, it will not be closed.
     */
    @Override
    public void close() throws IOException {
        if (ownedHttpClient && httpClient != null) {
            httpClient.close();
        }
    }

    /**
     * Builder for creating AemUploadSdk instances.
     */
    public static final class Builder {
        private ApiServerConfiguration serverConfig;
        private AuthConfig authConfig;
        private CloseableHttpClient httpClient;
        private HttpClient5BuilderFactory httpClient5BuilderFactory;
        private HttpClient5BuilderConfigurator httpClient5BuilderConfigurator;
        private FileSplitter fileSplitter;
        private PathNormalizer pathNormalizer;
        private boolean ownedHttpClient = true;

        private Builder() {
        }

        /**
         * Sets the server URL.
         *
         * @param serverUrl the full server URL (e.g., "https://author.adobeaemcloud.com")
         * @return this builder
         */
        public Builder serverUrl(String serverUrl) {
            this.serverConfig = ServerConfig.fromUrl(serverUrl);
            return this;
        }

        /**
         * Sets the server configuration.
         *
         * @param serverConfig the server configuration
         * @return this builder
         */
        public Builder serverConfig(ApiServerConfiguration serverConfig) {
            this.serverConfig = serverConfig;
            return this;
        }

        /**
         * Configures authentication using an access token.
         * Suitable for local development with developer tokens.
         *
         * @param accessToken the access token
         * @return this builder
         */
        public Builder withAccessToken(String accessToken) {
            this.authConfig = AccessTokenAuthConfig.of(accessToken);
            return this;
        }

        /**
         * Configures authentication using service credentials (JWT).
         * Suitable for server-to-server authentication.
         *
         * @param serviceCredentials the service credentials configuration
         * @return this builder
         */
        public Builder withServiceCredentials(ServiceCredentialsAuthConfig serviceCredentials) {
            this.authConfig = serviceCredentials;
            return this;
        }

        /**
         * Configures authentication using basic auth (username/password).
         * Suitable for on-premise AEM instances.
         *
         * @param username the username
         * @param password the password
         * @return this builder
         */
        public Builder withBasicAuth(String username, String password) {
            this.authConfig = BasicAuthConfig.of(username, password);
            return this;
        }

        /**
         * Sets the authentication configuration.
         *
         * @param authConfig the authentication configuration
         * @return this builder
         */
        public Builder authConfig(AuthConfig authConfig) {
            this.authConfig = authConfig;
            return this;
        }

        /**
         * Sets a custom HTTP client.
         * The SDK will not close this client; the caller is responsible for cleanup.
         * If you want the SDK to build the client (e.g., via OSGi-managed customization),
         * prefer {@link #httpClientBuilderFactory(HttpClient5BuilderFactory)} instead.
         *
         * @param httpClient the HTTP client to use
         * @return this builder
         */
        public Builder httpClient(CloseableHttpClient httpClient) {
            this.httpClient = httpClient;
            this.ownedHttpClient = false;
            return this;
        }

        /**
         * Sets a factory for creating HTTP client builders.
         * This is the preferred way to create managed/customized clients (e.g., in OSGi).
         *
         * @param httpClient5BuilderFactory the HTTP client builder factory
         * @return this builder
         */
        public Builder httpClientBuilderFactory(HttpClient5BuilderFactory httpClient5BuilderFactory) {
            this.httpClient5BuilderFactory = httpClient5BuilderFactory;
            return this;
        }

        /**
         * Sets a configurator to customize the HTTP client builder created by the SDK.
         * The configurator is applied after SDK authentication interceptors are added.
         *
         * @param httpClient5BuilderConfigurator the HTTP client builder configurator
         * @return this builder
         */
        public Builder httpClientBuilderConfigurator(HttpClient5BuilderConfigurator httpClient5BuilderConfigurator) {
            this.httpClient5BuilderConfigurator = httpClient5BuilderConfigurator;
            return this;
        }

        /**
         * Sets a custom file splitter for chunked uploads.
         *
         * @param fileSplitter the file splitter implementation
         * @return this builder
         */
        public Builder fileSplitter(FileSplitter fileSplitter) {
            this.fileSplitter = fileSplitter;
            return this;
        }

        /**
         * Sets a custom path normalizer.
         *
         * @param pathNormalizer the path normalizer implementation
         * @return this builder
         */
        public Builder pathNormalizer(PathNormalizer pathNormalizer) {
            this.pathNormalizer = pathNormalizer;
            return this;
        }

        /**
         * Builds the AemUploadSdk instance.
         *
         * @return a new AemUploadSdk instance
         * @throws IllegalStateException if required configuration is missing
         */
        public AemUploadSdk build() {
            validate();

            // Set defaults
            if (fileSplitter == null) {
                fileSplitter = new FileSplitterImpl();
            }
            if (pathNormalizer == null) {
                pathNormalizer = new PathNormalizerImpl();
            }

            // Create HTTP client with auth if not provided
            if (httpClient == null) {
                httpClient = createHttpClient();
                ownedHttpClient = true;
            }

            return new AemUploadSdk(this);
        }

        private void validate() {
            if (serverConfig == null) {
                throw new IllegalStateException("serverUrl or serverConfig must be set");
            }
            if (authConfig == null) {
                throw new IllegalStateException("authentication must be configured (use withAccessToken, withBasicAuth, or withServiceCredentials)");
            }
        }

        private CloseableHttpClient createHttpClient() {
            HttpClientBuilder builder = httpClient5BuilderFactory != null
                    ? httpClient5BuilderFactory.create()
                    : HttpClients.custom();

            // Configure authentication interceptor based on auth type
            if (authConfig instanceof AccessTokenAuthConfig) {
                AccessTokenAuthConfig tokenAuth = (AccessTokenAuthConfig) authConfig;
                ApiAccessTokenProvider tokenProvider = tokenAuth::getAccessToken;
                builder.addRequestInterceptorFirst(new ApiAuthorizationInterceptorImpl(tokenProvider));
            } else if (authConfig instanceof ServiceCredentialsAuthConfig) {
                ServiceCredentialsAuthConfig serviceAuth = (ServiceCredentialsAuthConfig) authConfig;
                ApiAccessTokenProvider tokenProvider = new ServiceCredentialsApiAccessTokenProvider(serviceAuth);
                builder.addRequestInterceptorFirst(new ApiAuthorizationInterceptorImpl(tokenProvider));
            } else if (authConfig instanceof BasicAuthConfig) {
                BasicAuthConfig basicAuth = (BasicAuthConfig) authConfig;
                // For basic auth, add a simple interceptor
                String credentials = basicAuth.getUsername() + ":" + basicAuth.getPassword();
                String encodedCredentials = Base64.getEncoder().encodeToString(
                        credentials.getBytes(StandardCharsets.UTF_8));
                builder.addRequestInterceptorFirst((request, entity, context) -> {
                    if (!request.containsHeader("Authorization")) {
                        request.setHeader("Authorization", "Basic " + encodedCredentials);
                    }
                });
            }

            if (httpClient5BuilderConfigurator != null) {
                builder = httpClient5BuilderConfigurator.configure(builder);
            }

            return builder.build();
        }
    }
}
