package com.kdiachenko.aemupload.config.data;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiAccessTokenPropertiesTest {

    @Test
    void shouldCreatePropertiesWithCorrectValues() {
        // Given
        String localDevelopmentAccessToken = "local-token";
        String imsEndpoint = "https://ims.example.com";
        List<String> metaScopes = Arrays.asList("scope1", "scope2");
        String clientId = "client-id";
        String clientSecret = "client-secret";
        String email = "test@example.com";
        String id = "test-id";
        String org = "test-org";
        String privateKeyFilePath = "/path/to/key.pem";
        String privateKeyContent = "private-key-content";
        int tokenLifeTimeInSec = 3600;

        // When
        ApiAccessTokenProperties properties = new ApiAccessTokenProperties(
                localDevelopmentAccessToken,
                imsEndpoint,
                metaScopes,
                clientId,
                clientSecret,
                email,
                id,
                org,
                privateKeyFilePath,
                privateKeyContent,
                tokenLifeTimeInSec
        );

        // Then
        assertThat(properties.getLocalDevelopmentAccessToken()).isEqualTo(localDevelopmentAccessToken);
        assertThat(properties.getImsEndpoint()).isEqualTo(imsEndpoint);
        assertThat(properties.getMetaScopes()).isEqualTo(metaScopes);
        assertThat(properties.getClientId()).isEqualTo(clientId);
        assertThat(properties.getClientSecret()).isEqualTo(clientSecret);
        assertThat(properties.getEmail()).isEqualTo(email);
        assertThat(properties.getId()).isEqualTo(id);
        assertThat(properties.getOrg()).isEqualTo(org);
        assertThat(properties.getPrivateKeyFilePath()).isEqualTo(privateKeyFilePath);
        assertThat(properties.getPrivateKeyContent()).isEqualTo(privateKeyContent);
        assertThat(properties.getTokenLifeTimeInSec()).isEqualTo(tokenLifeTimeInSec);
    }

    @Test
    void shouldImplementApiAccessTokenConfiguration() {
        // Given
        ApiAccessTokenProperties properties = new ApiAccessTokenProperties(
                "token", "endpoint", Arrays.asList("scope"), "id", "secret",
                "email", "id", "org", "path", "content", 3600
        );

        // Then
        assertThat(properties).isInstanceOf(com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration.class);
    }
}
