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
}
