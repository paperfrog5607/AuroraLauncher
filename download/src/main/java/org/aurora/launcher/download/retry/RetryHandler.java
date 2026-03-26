package org.aurora.launcher.download.retry;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RetryHandler {
    private final RetryPolicy policy;

    public RetryHandler(RetryPolicy policy) {
        this.policy = policy;
    }

    public Duration calculateDelay(int attempt) {
        switch (policy.getStrategy()) {
            case FIXED:
                return policy.getInitialDelay();
            case LINEAR:
                return BackoffStrategy.linear(attempt, policy.getInitialDelay());
            case EXPONENTIAL:
                Duration delay = BackoffStrategy.exponential(attempt, policy.getInitialDelay(), policy.getMultiplier());
                if (delay.compareTo(policy.getMaxDelay()) > 0) {
                    return policy.getMaxDelay();
                }
                return delay;
            default:
                return policy.getInitialDelay();
        }
    }

    public <T> CompletableFuture<T> executeWithRetry(
            Supplier<CompletableFuture<T>> action,
            Predicate<Exception> shouldRetry) {
        CompletableFuture<T> result = new CompletableFuture<>();
        executeWithRetryInternal(action, shouldRetry, 0, result);
        return result;
    }

    private <T> void executeWithRetryInternal(
            Supplier<CompletableFuture<T>> action,
            Predicate<Exception> shouldRetry,
            int attempt,
            CompletableFuture<T> result) {
        action.get().whenComplete((value, error) -> {
            if (error == null) {
                result.complete(value);
            } else {
                Exception ex = error instanceof Exception ? (Exception) error : new Exception(error);
                if (attempt < policy.getMaxRetries() && shouldRetry.test(ex)) {
                    Duration delay = calculateDelay(attempt + 1);
                    CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(delay.toMillis());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            result.completeExceptionally(e);
                            return;
                        }
                        executeWithRetryInternal(action, shouldRetry, attempt + 1, result);
                    });
                } else {
                    result.completeExceptionally(ex);
                }
            }
        });
    }
}