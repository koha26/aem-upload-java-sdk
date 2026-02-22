package com.kdiachenko.aemupload.auth;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ClockTest {

    @Test
    void systemClock_shouldReturnCurrentTime() {
        Clock clock = Clock.systemClock();

        Instant before = Instant.now();
        Instant now = clock.now();
        Instant after = Instant.now();

        assertThat(now).isBetween(before.minusSeconds(1), after.plusSeconds(1));
    }

    @Test
    void fixedClock_shouldReturnFixedInstant() {
        Instant fixed = Instant.parse("2024-01-01T00:00:00Z");
        Clock clock = Clock.fixed(fixed);

        assertThat(clock.now()).isEqualTo(fixed);
    }
}
