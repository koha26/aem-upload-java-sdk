package com.kdiachenko.aemupload;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.config.AccessTokenAuthConfig;
import com.kdiachenko.aemupload.config.BasicAuthConfig;
import com.kdiachenko.aemupload.config.ServerConfig;
import com.kdiachenko.aemupload.config.ServiceCredentialsAuthConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AemUploadSdkTest {

    @Test
    void builder_shouldCreateSdkWithAccessToken() throws IOException {
        try (AemUploadSdk sdk = AemUploadSdk.builder()
                .serverUrl("https://author.adobeaemcloud.com")
                .withAccessToken("test-token")
                .build()) {

            assertThat(sdk).isNotNull();
            assertThat(sdk.getServerConfig()).isNotNull();
            assertThat(sdk.getServerConfig().getHost()).isEqualTo("author.adobeaemcloud.com");
        }
    }

    @Test
    void builder_shouldCreateSdkWithBasicAuth() throws IOException {
        try (AemUploadSdk sdk = AemUploadSdk.builder()
                .serverUrl("http://localhost:4502")
                .withBasicAuth("admin", "admin")
                .build()) {

            assertThat(sdk).isNotNull();
            assertThat(sdk.getServerConfig().getHost()).isEqualTo("localhost");
        }
    }

    @Test
    void builder_shouldCreateSdkWithServiceCredentials() throws IOException {
        ServiceCredentialsAuthConfig authConfig = ServiceCredentialsAuthConfig.builder()
                .clientId("client-id")
                .clientSecret("client-secret")
                .technicalAccountId("tech-account")
                .orgId("org-id@AdobeOrg")
                .privateKeyContent("-----BEGIN RSA PRIVATE KEY-----\ntest\n-----END RSA PRIVATE KEY-----")
                .metaScopes(List.of("ent_aem_cloud_api"))
                .build();

        try (AemUploadSdk sdk = AemUploadSdk.builder()
                .serverUrl("https://author.adobeaemcloud.com")
                .withServiceCredentials(authConfig)
                .build()) {

            assertThat(sdk).isNotNull();
        }
    }

    @Test
    void builder_shouldFailWithoutServerUrl() {
        assertThatThrownBy(() -> AemUploadSdk.builder()
                .withAccessToken("test-token")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("serverUrl");
    }

    @Test
    void builder_shouldFailWithoutAuth() {
        assertThatThrownBy(() -> AemUploadSdk.builder()
                .serverUrl("https://example.com")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("authentication");
    }

    @Test
    void sdk_shouldProvideAllApis() throws IOException {
        try (AemUploadSdk sdk = AemUploadSdk.builder()
                .serverUrl("https://example.com")
                .withAccessToken("token")
                .build()) {

            DirectBinaryUploadApi uploadApi = sdk.directBinaryUploadApi();
            AssetFolderApi folderApi = sdk.assetFolderApi();
            AssetMetadataApi metadataApi = sdk.assetMetadataApi();

            assertThat(uploadApi).isNotNull();
            assertThat(folderApi).isNotNull();
            assertThat(metadataApi).isNotNull();

            // Calling again should return same instance (lazy singleton)
            assertThat(sdk.directBinaryUploadApi()).isSameAs(uploadApi);
            assertThat(sdk.assetFolderApi()).isSameAs(folderApi);
            assertThat(sdk.assetMetadataApi()).isSameAs(metadataApi);
        }
    }

    @Test
    void serverConfig_shouldParseUrlCorrectly() {
        ServerConfig config = ServerConfig.fromUrl("https://author.adobeaemcloud.com:443");

        assertThat(config.getSchema()).isEqualTo("https");
        assertThat(config.getHost()).isEqualTo("author.adobeaemcloud.com");
        assertThat(config.getPortAsInt()).isEqualTo(443);
        assertThat(config.getHostUrl()).isEqualTo("https://author.adobeaemcloud.com:443");
    }

    @Test
    void serverConfig_shouldHandleUrlWithoutPort() {
        ServerConfig config = ServerConfig.fromUrl("https://example.com");

        assertThat(config.getSchema()).isEqualTo("https");
        assertThat(config.getHost()).isEqualTo("example.com");
        assertThat(config.getPortAsInt()).isNull();
        assertThat(config.getHostUrl()).isEqualTo("https://example.com");
    }

    @Test
    void accessTokenAuthConfig_shouldValidate() {
        assertThatThrownBy(() -> AccessTokenAuthConfig.of(null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> AccessTokenAuthConfig.of("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void basicAuthConfig_shouldStoreCredentials() {
        BasicAuthConfig config = BasicAuthConfig.of("admin", "secret123");

        assertThat(config.getUsername()).isEqualTo("admin");
        assertThat(config.getPassword()).isEqualTo("secret123");
        assertThat(config.getAuthType()).isEqualTo("BasicAuth");
        // Verify password is not exposed in toString
        assertThat(config.toString()).doesNotContain("secret123");
        assertThat(config.toString()).contains("[REDACTED]");
    }

    @Test
    void serviceCredentialsAuthConfig_shouldValidate() {
        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("id")
                .clientSecret("secret")
                .technicalAccountId("tech")
                // missing orgId
                .privateKeyContent("key")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("orgId");
    }
}
