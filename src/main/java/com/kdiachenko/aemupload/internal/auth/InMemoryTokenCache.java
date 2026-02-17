package com.kdiachenko.aemupload.internal.auth;

import com.kdiachenko.aemupload.auth.Clock;
import com.kdiachenko.aemupload.auth.TokenCache;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe in-memory implementation of TokenCache.
 */
public class InMemoryTokenCache implements TokenCache {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Clock clock;

    private String token;
    private Instant expiration;

    public InMemoryTokenCache() {
        this(Clock.systemClock());
    }

    public InMemoryTokenCache(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Optional<String> get() {
        lock.readLock().lock();
        try {
            if (token != null && expiration != null && clock.now().isBefore(expiration)) {
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
            expiration = clock.now().plus(ttl);
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
