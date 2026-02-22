package com.kdiachenko.aemupload.auth;

/**
 * Provides access tokens for authenticated API calls.
 *
 * <p>Implementations may retrieve tokens from configuration, caches,
 * or external services (e.g., Adobe IMS).</p>
 */
public interface ApiAccessTokenProvider {
    /**
     * Returns an access token to be used for authenticated API calls.
     *
     * @return access token string (may be null if unavailable)
     */
    String getAccessToken();
}
