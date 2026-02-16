package com.kdiachenko.aemupload.spring;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AemUploadSdkPropertiesTest {

    @Test
    void properties_shouldHaveDefaultsAndAllowOverrides() {
        AemUploadSdkProperties props = new AemUploadSdkProperties();

        assertThat(props.isEnabled()).isTrue();
        assertThat(props.getAuthType()).isEqualTo(AemUploadSdkProperties.AuthType.BASIC);
        assertThat(props.getUsername()).isEqualTo("admin");
        assertThat(props.getPassword()).isEqualTo("admin");

        props.setEnabled(false);
        props.setServerUrl("https://example.com");
        props.setAuthType(AemUploadSdkProperties.AuthType.ACCESS_TOKEN);
        props.setAccessToken("token");

        assertThat(props.isEnabled()).isFalse();
        assertThat(props.getServerUrl()).isEqualTo("https://example.com");
        assertThat(props.getAccessToken()).isEqualTo("token");

        AemUploadSdkProperties.ServiceCredentials creds = props.getServiceCredentials();
        creds.setClientId("client");
        creds.setClientSecret("secret");
        creds.setTechnicalAccountId("tech");
        creds.setOrgId("org");
        creds.setPrivateKeyContent("key");
        creds.setMetaScopes(List.of("scope"));
        creds.setImsEndpoint("https://ims.example.com");

        assertThat(creds.getClientId()).isEqualTo("client");
        assertThat(creds.getClientSecret()).isEqualTo("secret");
        assertThat(creds.getTechnicalAccountId()).isEqualTo("tech");
        assertThat(creds.getOrgId()).isEqualTo("org");
        assertThat(creds.getPrivateKeyContent()).isEqualTo("key");
        assertThat(creds.getMetaScopes()).contains("scope");
        assertThat(creds.getImsEndpoint()).isEqualTo("https://ims.example.com");
    }
}
