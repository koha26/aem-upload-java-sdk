package com.kdiachenko.aemupload.config;

import java.util.Objects;

/**
 * Authentication configuration using a static access token.
 * Useful for local development with developer tokens.
 *
 * <pre>{@code
 * AuthConfig auth = AccessTokenAuthConfig.of("your-dev-token");
 * }</pre>
 */
public final class AccessTokenAuthConfig implements AuthConfig {

    private final String accessToken;

    private AccessTokenAuthConfig(String accessToken) {
        this.accessToken = Objects.requireNonNull(accessToken, "accessToken must not be null");
        if (accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken must not be blank");
        }
    }

    /**
     * Creates an AccessTokenAuthConfig with the given token.
     *
     * @param accessToken the access token
     * @return a new AccessTokenAuthConfig
     */
    public static AccessTokenAuthConfig of(String accessToken) {
        return new AccessTokenAuthConfig(accessToken);
    }

    /**
     * Returns the access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getAuthType() {
        return "AccessToken";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccessTokenAuthConfig that = (AccessTokenAuthConfig) o;
        return Objects.equals(accessToken, that.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken);
    }

    @Override
    public String toString() {
        return "AccessTokenAuthConfig{accessToken=[REDACTED]}";
    }
}
