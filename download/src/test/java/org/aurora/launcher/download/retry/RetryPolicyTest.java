package org.aurora.launcher.download.retry;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class RetryPolicyTest {

    @Test
    void defaultValues() {
        RetryPolicy policy = new RetryPolicy();
        
        assertEquals(3, policy.getMaxRetries());
        assertEquals(Duration.ofSeconds(1), policy.getInitialDelay());
        assertEquals(Duration.ofSeconds(30), policy.getMaxDelay());
        assertEquals(2.0, policy.getMultiplier());
        assertEquals(RetryPolicy.BackoffStrategy.EXPONENTIAL, policy.getStrategy());
    }

    @Test
    void settersAndGetters() {
        RetryPolicy policy = new RetryPolicy();
        
        policy.setMaxRetries(5);
        policy.setInitialDelay(Duration.ofSeconds(2));
        policy.setMaxDelay(Duration.ofSeconds(60));
        policy.setMultiplier(1.5);
        policy.setStrategy(RetryPolicy.BackoffStrategy.LINEAR);
        
        assertEquals(5, policy.getMaxRetries());
        assertEquals(Duration.ofSeconds(2), policy.getInitialDelay());
        assertEquals(Duration.ofSeconds(60), policy.getMaxDelay());
        assertEquals(1.5, policy.getMultiplier());
        assertEquals(RetryPolicy.BackoffStrategy.LINEAR, policy.getStrategy());
    }

    @Test
    void backoffStrategyEnum() {
        assertEquals(3, RetryPolicy.BackoffStrategy.values().length);
        assertEquals(RetryPolicy.BackoffStrategy.FIXED, RetryPolicy.BackoffStrategy.valueOf("FIXED"));
        assertEquals(RetryPolicy.BackoffStrategy.LINEAR, RetryPolicy.BackoffStrategy.valueOf("LINEAR"));
        assertEquals(RetryPolicy.BackoffStrategy.EXPONENTIAL, RetryPolicy.BackoffStrategy.valueOf("EXPONENTIAL"));
    }
}