package org.aurora.launcher.download.retry;

import java.time.Duration;

public class RetryPolicy {
    public enum BackoffStrategy {
        FIXED, LINEAR, EXPONENTIAL
    }

    private int maxRetries = 3;
    private Duration initialDelay = Duration.ofSeconds(1);
    private Duration maxDelay = Duration.ofSeconds(30);
    private double multiplier = 2.0;
    private BackoffStrategy strategy = BackoffStrategy.EXPONENTIAL;

    public RetryPolicy() {
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(Duration initialDelay) {
        this.initialDelay = initialDelay;
    }

    public Duration getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(Duration maxDelay) {
        this.maxDelay = maxDelay;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public BackoffStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(BackoffStrategy strategy) {
        this.strategy = strategy;
    }
}