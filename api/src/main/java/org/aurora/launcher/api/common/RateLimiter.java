package org.aurora.launcher.api.common;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RateLimiter {
    
    private final Semaphore semaphore;
    private final int maxRequests;
    private final Duration period;
    
    public RateLimiter(int maxRequests, Duration period) {
        this.maxRequests = maxRequests;
        this.period = period;
        this.semaphore = new Semaphore(maxRequests);
    }
    
    public void acquire() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while acquiring rate limit permit", e);
        }
    }
    
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        return semaphore.tryAcquire(timeout, unit);
    }
    
    public void release() {
        semaphore.release();
    }
    
    public int availablePermits() {
        return semaphore.availablePermits();
    }
    
    public int getMaxRequests() {
        return maxRequests;
    }
    
    public Duration getPeriod() {
        return period;
    }
}