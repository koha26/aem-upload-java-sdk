package com.kdiachenko.aemupload.config;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Immutable configuration for the AEM server connection.
 * Use the builder to create instances.
 *
 * <pre>{@code
 * ServerConfig config = ServerConfig.builder()
 *     .schema("https")
 *     .host("author.adobeaemcloud.com")
 *     .port(443)
 *     .build();
 * }</pre>
 */
@Value
@Builder
public class ServerConfig implements ApiServerConfiguration {

    String schema;
    String host;
    Integer port;

    private ServerConfig(ServerConfigBuilder builder) {
        this.schema = builder.schema;
        this.host = builder.host;
        this.port = builder.port;
    }

    /**
     * Creates a ServerConfig from a full URL.
     *
     * @param serverUrl the server URL (e.g., "https://author.adobeaemcloud.com:443")
     * @return a new ServerConfig
     * @throws IllegalArgumentException if the URL is invalid
     */
    public static ServerConfig fromUrl(String serverUrl) {
        Objects.requireNonNull(serverUrl, "serverUrl must not be null");

        try {
            java.net.URI uri = java.net.URI.create(serverUrl);
            var builder = builder()
                    .schema(uri.getScheme())
                    .host(uri.getHost());

            if (uri.getPort() > 0) {
                builder.port(uri.getPort());
            }

            return builder.build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid server URL: " + serverUrl, e);
        }
    }

    /**
     * Returns the port as an integer, or null if not set.
     *
     * @return the port number or null
     */
    public Integer getPortAsInt() {
        return port;
    }

    @Override
    public String getHostUrl() {
        StringBuilder url = new StringBuilder();
        url.append(schema).append("://").append(host);
        if (port != null) {
            url.append(":").append(port);
        }
        return url.toString();
    }

    /**
     * Builder for ServerConfig.
     */
    static class ServerConfigBuilder {

        /**
         * Builds the ServerConfig.
         *
         * @return a new ServerConfig instance
         * @throws IllegalStateException if required fields are missing
         */
        public ServerConfig build() {
            if (schema == null || schema.isBlank()) {
                throw new IllegalStateException("scheme must not be null or blank");
            }
            if (host == null || host.isBlank()) {
                throw new IllegalStateException("host must not be null or blank");
            }
            return new ServerConfig(this);
        }
    }
}
