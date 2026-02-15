package com.kdiachenko.aemupload.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServerConfigTest {

    @Test
    void builder_shouldRequireSchema() {
        assertThatThrownBy(() -> ServerConfig.builder()
                .host("example.com")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("scheme");
    }

    @Test
    void builder_shouldRequireHost() {
        assertThatThrownBy(() -> ServerConfig.builder()
                .schema("https")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("host");
    }

    @Test
    void fromUrl_shouldThrowOnInvalid() {
        assertThatThrownBy(() -> ServerConfig.fromUrl("://bad"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid server URL");
    }

    @Test
    void getHostUrl_shouldIncludePortWhenSet() {
        ServerConfig config = ServerConfig.builder()
                .schema("https")
                .host("example.com")
                .port("443")
                .build();

        assertThat(config.getHostUrl()).isEqualTo("https://example.com:443");
    }
}
