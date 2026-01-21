package com.kdiachenko.aemupload.internal.auth;

import com.kdiachenko.aemupload.auth.TokenCache;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe in-memory implementation of TokenCache.
 */
public class InMemoryTokenCache implements TokenCache {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private Instant expiration;

    @Override
    public Optional<String> get() {
        lock.readLock().lock();
        try {
            if (token != null && expiration != null && Instant.now().isBefore(expiration)) {
                return Optional.of(token);
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void put(final String token, final Duration ttl) {
        lock.writeLock().lock();
        try {
            this.token = token;
            expiration = Instant.now().plus(ttl);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            this.token = null;
            expiration = null;
        } finally {
            lock.writeLock().unlock();
        }
    }
}

