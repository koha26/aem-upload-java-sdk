package com.kdiachenko.aemupload.internal.auth;

import com.kdiachenko.aemupload.auth.Clock;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTokenCacheTest {

    @Test
    void get_shouldReturnEmptyWhenNoToken() {
        InMemoryTokenCache cache = new InMemoryTokenCache(Clock.fixed(Instant.parse("2024-01-01T00:00:00Z")));

        assertThat(cache.get()).isEmpty();
    }

    @Test
    void get_shouldReturnTokenWhenNotExpired() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"));
        InMemoryTokenCache cache = new InMemoryTokenCache(clock);

        cache.put("token", Duration.ofSeconds(10));

        assertThat(cache.get()).contains("token");
    }

    @Test
    void get_shouldReturnEmptyWhenExpired() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"));
        InMemoryTokenCache cache = new InMemoryTokenCache(clock);

        cache.put("token", Duration.ofSeconds(10));
        clock.advance(Duration.ofSeconds(11));

        assertThat(cache.get()).isEmpty();
    }

    @Test
    void clear_shouldRemoveToken() {
        InMemoryTokenCache cache = new InMemoryTokenCache(Clock.fixed(Instant.parse("2024-01-01T00:00:00Z")));

        cache.put("token", Duration.ofSeconds(10));
        cache.clear();

        assertThat(cache.get()).isEmpty();
    }

    @Test
    void get_shouldReturnEmptyWhenTokenExistsButExpirationMissing() throws Exception {
        InMemoryTokenCache cache = new InMemoryTokenCache(Clock.fixed(Instant.parse("2024-01-01T00:00:00Z")));

        Field tokenField = InMemoryTokenCache.class.getDeclaredField("token");
        tokenField.setAccessible(true);
        tokenField.set(cache, "token");

        Field expirationField = InMemoryTokenCache.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(cache, null);

        assertThat(cache.get()).isEmpty();
    }

    static class MutableClock implements Clock {
        private Instant now;

        MutableClock(Instant now) {
            this.now = now;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public Instant now() {
            return now;
        }
    }
}
