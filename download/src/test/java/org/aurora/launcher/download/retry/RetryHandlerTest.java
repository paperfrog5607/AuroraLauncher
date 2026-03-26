package org.aurora.launcher.download.retry;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class RetryHandlerTest {

    @Test
    void calculateDelayFixed() {
        RetryPolicy policy = new RetryPolicy();
        policy.setStrategy(RetryPolicy.BackoffStrategy.FIXED);
        policy.setInitialDelay(Duration.ofSeconds(5));
        
        RetryHandler handler = new RetryHandler(policy);
        
        assertEquals(Duration.ofSeconds(5), handler.calculateDelay(1));
        assertEquals(Duration.ofSeconds(5), handler.calculateDelay(2));
        assertEquals(Duration.ofSeconds(5), handler.calculateDelay(3));
    }

    @Test
    void calculateDelayLinear() {
        RetryPolicy policy = new RetryPolicy();
        policy.setStrategy(RetryPolicy.BackoffStrategy.LINEAR);
        policy.setInitialDelay(Duration.ofSeconds(2));
        
        RetryHandler handler = new RetryHandler(policy);
        
        assertEquals(Duration.ofSeconds(2), handler.calculateDelay(1));
        assertEquals(Duration.ofSeconds(4), handler.calculateDelay(2));
        assertEquals(Duration.ofSeconds(6), handler.calculateDelay(3));
    }

    @Test
    void calculateDelayExponential() {
        RetryPolicy policy = new RetryPolicy();
        policy.setStrategy(RetryPolicy.BackoffStrategy.EXPONENTIAL);
        policy.setInitialDelay(Duration.ofSeconds(1));
        policy.setMultiplier(2.0);
        policy.setMaxDelay(Duration.ofSeconds(30));
        
        RetryHandler handler = new RetryHandler(policy);
        
        assertEquals(Duration.ofSeconds(1), handler.calculateDelay(1));
        assertEquals(Duration.ofSeconds(2), handler.calculateDelay(2));
        assertEquals(Duration.ofSeconds(4), handler.calculateDelay(3));
        assertEquals(Duration.ofSeconds(8), handler.calculateDelay(4));
    }

    @Test
    void calculateDelayExponentialCappedAtMax() {
        RetryPolicy policy = new RetryPolicy();
        policy.setStrategy(RetryPolicy.BackoffStrategy.EXPONENTIAL);
        policy.setInitialDelay(Duration.ofSeconds(10));
        policy.setMultiplier(3.0);
        policy.setMaxDelay(Duration.ofSeconds(30));
        
        RetryHandler handler = new RetryHandler(policy);
        
        assertEquals(Duration.ofSeconds(10), handler.calculateDelay(1));
        assertEquals(Duration.ofSeconds(30), handler.calculateDelay(2));
        assertEquals(Duration.ofSeconds(30), handler.calculateDelay(3));
    }

    @Test
    void executeWithRetrySuccess() throws Exception {
        RetryPolicy policy = new RetryPolicy();
        policy.setMaxRetries(3);
        RetryHandler handler = new RetryHandler(policy);
        
        AtomicInteger attempts = new AtomicInteger(0);
        
        String result = handler.executeWithRetry(
            () -> {
                attempts.incrementAndGet();
                return CompletableFuture.completedFuture("success");
            },
            e -> true
        ).join();
        
        assertEquals("success", result);
        assertEquals(1, attempts.get());
    }

    @Test
    void executeWithRetryFailuresThenSuccess() throws Exception {
        RetryPolicy policy = new RetryPolicy();
        policy.setMaxRetries(3);
        policy.setInitialDelay(Duration.ofMillis(10));
        RetryHandler handler = new RetryHandler(policy);
        
        AtomicInteger attempts = new AtomicInteger(0);
        
        String result = handler.executeWithRetry(
            () -> {
                int attempt = attempts.incrementAndGet();
                if (attempt < 3) {
                    CompletableFuture<String> failed = new CompletableFuture<>();
                    failed.completeExceptionally(new IOException("fail"));
                    return failed;
                }
                return CompletableFuture.completedFuture("success");
            },
            e -> e instanceof IOException
        ).join();
        
        assertEquals("success", result);
        assertEquals(3, attempts.get());
    }

    @Test
    void executeWithRetryMaxRetriesExceeded() {
        RetryPolicy policy = new RetryPolicy();
        policy.setMaxRetries(2);
        policy.setInitialDelay(Duration.ofMillis(10));
        RetryHandler handler = new RetryHandler(policy);
        
        AtomicInteger attempts = new AtomicInteger(0);
        
        CompletableFuture<String> future = handler.executeWithRetry(
            () -> {
                attempts.incrementAndGet();
                CompletableFuture<String> failed = new CompletableFuture<>();
                failed.completeExceptionally(new IOException("fail"));
                return failed;
            },
            e -> e instanceof IOException
        );
        
        assertThrows(Exception.class, future::join);
        assertEquals(3, attempts.get());
    }
}