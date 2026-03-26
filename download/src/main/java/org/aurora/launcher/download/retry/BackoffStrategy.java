package org.aurora.launcher.download.retry;

import java.time.Duration;

public class BackoffStrategy {
    private BackoffStrategy() {
    }

    public static Duration fixed(Duration delay) {
        return delay;
    }

    public static Duration linear(int attempt, Duration initial) {
        return initial.multipliedBy(attempt);
    }

    public static Duration exponential(int attempt, Duration initial, double multiplier) {
        long delay = (long) (initial.toMillis() * Math.pow(multiplier, attempt - 1));
        return Duration.ofMillis(delay);
    }
}