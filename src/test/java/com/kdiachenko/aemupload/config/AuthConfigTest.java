package com.kdiachenko.aemupload.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthConfigTest {

    @Test
    void accessTokenAuthConfig_shouldExposeTokenAndRedactToString() {
        AccessTokenAuthConfig config = AccessTokenAuthConfig.of("token-123");

        assertThat(config.getAccessToken()).isEqualTo("token-123");
        assertThat(config.getAuthType()).isEqualTo("AccessToken");
        assertThat(config.toString()).doesNotContain("token-123");
        assertThat(config.toString()).contains("[REDACTED]");
    }

    @Test
    void accessTokenAuthConfig_shouldImplementEqualsAndHashCode() {
        AccessTokenAuthConfig config1 = AccessTokenAuthConfig.of("token-123");
        AccessTokenAuthConfig config2 = AccessTokenAuthConfig.of("token-123");
        AccessTokenAuthConfig config3 = AccessTokenAuthConfig.of("other");

        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
        assertThat(config1).isNotEqualTo(config3);
    }

    @Test
    void accessTokenAuthConfig_shouldRejectBlank() {
        assertThatThrownBy(() -> AccessTokenAuthConfig.of(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void basicAuthConfig_shouldRedactPassword() {
        BasicAuthConfig config = BasicAuthConfig.of("admin", "secret");

        assertThat(config.getUsername()).isEqualTo("admin");
        assertThat(config.getPassword()).isEqualTo("secret");
        assertThat(config.getAuthType()).isEqualTo("BasicAuth");
        assertThat(config.toString()).doesNotContain("secret");
        assertThat(config.toString()).contains("[****]");
    }

    @Test
    void basicAuthConfig_shouldRejectNulls() {
        assertThatThrownBy(() -> BasicAuthConfig.of(null, "secret"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> BasicAuthConfig.of("admin", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void serviceCredentialsAuthConfig_shouldRedactSecrets() {
        ServiceCredentialsAuthConfig config = ServiceCredentialsAuthConfig.builder()
                .clientId("client")
                .clientSecret("secret")
                .technicalAccountId("tech")
                .orgId("org")
                .privateKeyContent("-----BEGIN PRIVATE KEY-----key-----END PRIVATE KEY-----")
                .metaScopes(List.of("scope"))
                .build();

        assertThat(config.getAuthType()).isEqualTo("ServiceCredentials");
        assertThat(config.toString()).doesNotContain("secret");
        assertThat(config.toString()).doesNotContain("PRIVATE KEY");
    }
}
