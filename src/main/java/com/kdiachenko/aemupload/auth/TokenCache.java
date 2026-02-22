package com.kdiachenko.aemupload.auth;

import java.time.Duration;
import java.util.Optional;

/**
 * Interface for caching access tokens.
 * Implementations should handle token expiration and thread safety.
 */
public interface TokenCache {
    /**
     * Retrieves a cached token if it exists and is not expired.
     *
     * @return the cached token, or empty if not found or expired
     */
    Optional<String> get();

    /**
     * Stores a token in the cache with a time-to-live.
     *
     * @param token the token to cache
     * @param ttl the time-to-live duration
     */
    void put(String token, Duration ttl);

    /**
     * Clears the cache.
     */
    void clear();
}

