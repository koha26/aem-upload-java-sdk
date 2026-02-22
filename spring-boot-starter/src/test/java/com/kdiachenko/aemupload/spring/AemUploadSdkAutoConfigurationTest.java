package com.kdiachenko.aemupload.spring;

import com.kdiachenko.aemupload.AemUploadSdk;
import com.kdiachenko.aemupload.http.HttpClient5BuilderFactory;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AemUploadSdkAutoConfigurationTest {

    @Test
    void aemUploadSdk_shouldCreateWithAccessToken() throws Exception {
        AemUploadSdkProperties props = new AemUploadSdkProperties();
        props.setServerUrl("https://example.com");
        props.setAuthType(AemUploadSdkProperties.AuthType.ACCESS_TOKEN);
        props.setAccessToken("token");

        AemUploadSdkAutoConfiguration config = new AemUploadSdkAutoConfiguration(props);

        AemUploadSdk sdk = config.aemUploadSdk(Optional.empty());

        assertThat(sdk).isNotNull();
        config.destroy();
    }

    @Test
    void aemUploadSdk_shouldFailWhenAccessTokenMissing() {
        AemUploadSdkProperties props = new AemUploadSdkProperties();
        props.setServerUrl("https://example.com");
        props.setAuthType(AemUploadSdkProperties.AuthType.ACCESS_TOKEN);
        props.setAccessToken(" ");

        AemUploadSdkAutoConfiguration config = new AemUploadSdkAutoConfiguration(props);

        assertThatThrownBy(() -> config.aemUploadSdk(Optional.empty()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Access token is required");
    }

    @Test
    void aemUploadSdk_shouldCreateWithBasicAuth() throws Exception {
        AemUploadSdkProperties props = new AemUploadSdkProperties();
        props.setServerUrl("https://example.com");
        props.setAuthType(AemUploadSdkProperties.AuthType.BASIC);
        props.setUsername("user");
        props.setPassword("pass");

        AemUploadSdkAutoConfiguration config = new AemUploadSdkAutoConfiguration(props);

        AemUploadSdk sdk = config.aemUploadSdk(Optional.empty());

        assertThat(sdk).isNotNull();
        config.destroy();
    }

    @Test
    void aemUploadSdk_shouldUseCustomHttpClient5BuilderFactoryWhenProvided() throws Exception {
        AemUploadSdkProperties props = new AemUploadSdkProperties();
        props.setServerUrl("https://example.com");
        props.setAuthType(AemUploadSdkProperties.AuthType.ACCESS_TOKEN);
        props.setAccessToken("token");

        AemUploadSdkAutoConfiguration config = new AemUploadSdkAutoConfiguration(props);
        AtomicBoolean createCalled = new AtomicBoolean(false);
        HttpClient5BuilderFactory factory = () -> {
            createCalled.set(true);
            return HttpClients.custom();
        };

        AemUploadSdk sdk = config.aemUploadSdk(Optional.of(factory));

        assertThat(sdk).isNotNull();
        assertThat(createCalled).isTrue();
        config.destroy();
    }

    @Test
    void aemUploadSdk_shouldCreateWithServiceCredentialsPrivateKeyContent() throws Exception {
        AemUploadSdkProperties props = new AemUploadSdkProperties();
        props.setServerUrl("https://example.com");
        props.setAuthType(AemUploadSdkProperties.AuthType.SERVICE_CREDENTIALS);

        AemUploadSdkProperties.ServiceCredentials creds = props.getServiceCredentials();
        creds.setClientId("client");
        creds.setClientSecret("secret");
        creds.setTechnicalAccountId("tech");
        creds.setOrgId("org");
        creds.setPrivateKeyContent("key");
        creds.setMetaScopes(List.of("scope"));

        AemUploadSdkAutoConfiguration config = new AemUploadSdkAutoConfiguration(props);

        AemUploadSdk sdk = config.aemUploadSdk(Optional.empty());

        assertThat(sdk).isNotNull();
        config.destroy();
    }

    @Test
    void aemUploadSdk_shouldCreateWithServiceCredentialsPrivateKeyPath() throws Exception {
        AemUploadSdkProperties props = new AemUploadSdkProperties();
        props.setServerUrl("https://example.com");
        props.setAuthType(AemUploadSdkProperties.AuthType.SERVICE_CREDENTIALS);

        AemUploadSdkProperties.ServiceCredentials creds = props.getServiceCredentials();
        creds.setClientId("client");
        creds.setClientSecret("secret");
        creds.setTechnicalAccountId("tech");
        creds.setOrgId("org");
        creds.setPrivateKeyPath("/tmp/key.pem");
        creds.setMetaScopes(List.of("scope"));

        AemUploadSdkAutoConfiguration config = new AemUploadSdkAutoConfiguration(props);

        AemUploadSdk sdk = config.aemUploadSdk(Optional.empty());

        assertThat(sdk).isNotNull();
        config.destroy();
    }

    @Test
    void aemUploadSdk_shouldFailWhenServiceCredentialsMissingKey() {
        AemUploadSdkProperties props = new AemUploadSdkProperties();
        props.setServerUrl("https://example.com");
        props.setAuthType(AemUploadSdkProperties.AuthType.SERVICE_CREDENTIALS);

        AemUploadSdkProperties.ServiceCredentials creds = props.getServiceCredentials();
        creds.setClientId("client");
        creds.setClientSecret("secret");
        creds.setTechnicalAccountId("tech");
        creds.setOrgId("org");
        creds.setPrivateKeyContent(" ");
        creds.setPrivateKeyPath(" ");
        creds.setMetaScopes(List.of("scope"));

        AemUploadSdkAutoConfiguration config = new AemUploadSdkAutoConfiguration(props);

        assertThatThrownBy(() -> config.aemUploadSdk(Optional.empty()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Either private-key-content or private-key-path");
    }

    @Test
    void destroy_shouldHandleNullSdk() {
        AemUploadSdkProperties props = new AemUploadSdkProperties();
        AemUploadSdkAutoConfiguration config = new AemUploadSdkAutoConfiguration(props);

        config.destroy();
    }
}
