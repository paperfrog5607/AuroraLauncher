package org.aurora.launcher.api.common;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest {

    @Test
    void shouldCreateRateLimiter() {
        RateLimiter limiter = new RateLimiter(10, Duration.ofSeconds(1));
        
        assertEquals(10, limiter.getMaxRequests());
        assertEquals(Duration.ofSeconds(1), limiter.getPeriod());
    }

    @Test
    void shouldAcquirePermit() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(5, Duration.ofSeconds(1));
        
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryAcquire(100, TimeUnit.MILLISECONDS));
        }
    }

    @Test
    void shouldRejectWhenExceeded() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(2, Duration.ofSeconds(1));
        
        assertTrue(limiter.tryAcquire(100, TimeUnit.MILLISECONDS));
        assertTrue(limiter.tryAcquire(100, TimeUnit.MILLISECONDS));
        assertFalse(limiter.tryAcquire(100, TimeUnit.MILLISECONDS));
    }

    @Test
    void shouldReleasePermit() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(1, Duration.ofSeconds(1));
        
        assertTrue(limiter.tryAcquire(100, TimeUnit.MILLISECONDS));
        limiter.release();
        assertTrue(limiter.tryAcquire(100, TimeUnit.MILLISECONDS));
    }

    @Test
    void shouldGetAvailablePermits() {
        RateLimiter limiter = new RateLimiter(5, Duration.ofSeconds(1));
        
        assertEquals(5, limiter.availablePermits());
    }
}