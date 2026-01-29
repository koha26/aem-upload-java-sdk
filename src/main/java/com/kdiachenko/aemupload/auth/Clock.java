package com.kdiachenko.aemupload.auth;

import java.time.Instant;

/**
 * Abstraction for obtaining the current time.
 * This interface allows for easy testing by injecting a mock clock.
 */
@FunctionalInterface
public interface Clock {

    /**
     * Returns the current instant.
     *
     * @return the current instant
     */
    Instant now();

    /**
     * Returns the default system clock.
     *
     * @return a clock that returns the current system time
     */
    static Clock systemClock() {
        return Instant::now;
    }

    /**
     * Returns a fixed clock for testing.
     *
     * @param instant the fixed instant to return
     * @return a clock that always returns the given instant
     */
    static Clock fixed(Instant instant) {
        return () -> instant;
    }
}
