package com.kdiachenko.aemupload.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ApiServerConfigurationTest {

    @ParameterizedTest
    @MethodSource("provideServerConfigurations")
    void getHostUrl_shouldReturnCorrectUrl(String schema, String host, String port, String expectedUrl) {
        // Given
        ApiServerConfiguration configuration = new TestApiServerConfiguration(schema, host, port);

        // When
        String actualUrl = configuration.getHostUrl();

        // Then
        assertThat(actualUrl).isEqualTo(expectedUrl);
    }

    private static Stream<Arguments> provideServerConfigurations() {
        return Stream.of(
                Arguments.of("http", "example.com", "8080", "http://example.com:8080"),
                Arguments.of("https", "example.com", "", "https://example.com"),
                Arguments.of("https", "example.com", null, "https://example.com"),
                Arguments.of("http", "localhost", "80", "http://localhost:80")
        );
    }

    // Test implementation of ApiServerConfiguration
    private static class TestApiServerConfiguration implements ApiServerConfiguration {
        private final String schema;
        private final String host;
        private final String port;

        public TestApiServerConfiguration(String schema, String host, String port) {
            this.schema = schema;
            this.host = host;
            this.port = port;
        }

        @Override
        public String getSchema() {
            return schema;
        }

        @Override
        public String getHost() {
            return host;
        }

        @Override
        public String getPort() {
            return port;
        }
    }
}
