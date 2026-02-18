package com.kdiachenko.aemupload.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceCredentialsAuthConfigTest {

    @Test
    void builder_shouldRequireMetaScopes() {
        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of())
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("metaScopes");
    }

    @Test
    void builder_shouldRequirePrivateKey() {
        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("privateKey");
    }

    @Test
    void builder_shouldDefaultTokenLifetimeWhenNonPositive() {
        ServiceCredentialsAuthConfig config = ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .tokenLifetimeSeconds(0)
                .build();

        assertThat(config.getTokenLifeTimeInSec()).isGreaterThan(0);
    }

    @Test
    void builder_shouldKeepProvidedPositiveTokenLifetime() {
        ServiceCredentialsAuthConfig config = ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .tokenLifetimeSeconds(120)
                .build();

        assertThat(config.getTokenLifeTimeInSec()).isEqualTo(120);
    }

    @Test
    void builder_shouldAcceptPrivateKeyFilePath() {
        ServiceCredentialsAuthConfig config = ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyFilePath("/path/to/key.pem")
                .metaScopes(List.of("scope"))
                .build();

        assertThat(config.getPrivateKeyFilePath()).isEqualTo("/path/to/key.pem");
    }

    @Test
    void authConfigAccessors_shouldReturnApiAccessTokenFields() {
        ServiceCredentialsAuthConfig config = ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech-id")
                .orgId("org-id")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build();

        assertThat(config.getAuthType()).isEqualTo("ServiceCredentials");
        assertThat(config.getId()).isEqualTo("tech-id");
        assertThat(config.getOrg()).isEqualTo("org-id");
        assertThat(config.getLocalDevelopmentAccessToken()).isNull();
    }

    @Test
    void builder_shouldRejectBlankRequiredFields() {
        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId(null)
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clientId");

        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId(" ")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clientId");

        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret(null)
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clientSecret");

        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret(" ")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("clientSecret");

        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId(null)
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("technicalAccountId");

        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId(" ")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("technicalAccountId");

        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId(" ")
                .privateKeyContent("key")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("orgId");

        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent(" ")
                .privateKeyFilePath(" ")
                .metaScopes(List.of("scope"))
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("either privateKeyContent or privateKeyFilePath");

        assertThatThrownBy(() -> ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("key")
                .metaScopes(null)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("metaScopes");
    }
}
