package com.kdiachenko.aemupload.config;

import lombok.Value;

import java.util.Objects;

/**
 * Authentication configuration using basic authentication (username/password).
 * Useful for connecting to on-premise AEM instances.
 *
 * <pre>{@code
 * AuthConfig auth = BasicAuthConfig.of("admin", "admin");
 * }</pre>
 */
@Value
public class BasicAuthConfig implements AuthConfig {
    String username;
    String password;

    private BasicAuthConfig(String username, String password) {
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    /**
     * Creates a BasicAuthConfig with the given credentials.
     *
     * @param username the username
     * @param password the password
     * @return a new BasicAuthConfig
     */
    public static BasicAuthConfig of(String username, String password) {
        return new BasicAuthConfig(username, password);
    }

    @Override
    public String getAuthType() {
        return "BasicAuth";
    }
}
