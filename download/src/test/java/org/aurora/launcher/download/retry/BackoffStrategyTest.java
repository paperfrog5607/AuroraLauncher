package org.aurora.launcher.download.retry;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class BackoffStrategyTest {

    @Test
    void fixedStrategy() {
        Duration result = BackoffStrategy.fixed(Duration.ofSeconds(5));
        assertEquals(Duration.ofSeconds(5), result);
    }

    @Test
    void linearStrategy() {
        Duration initial = Duration.ofSeconds(2);
        
        assertEquals(Duration.ofSeconds(2), BackoffStrategy.linear(1, initial));
        assertEquals(Duration.ofSeconds(4), BackoffStrategy.linear(2, initial));
        assertEquals(Duration.ofSeconds(6), BackoffStrategy.linear(3, initial));
    }

    @Test
    void exponentialStrategy() {
        Duration initial = Duration.ofSeconds(1);
        double multiplier = 2.0;
        
        assertEquals(Duration.ofSeconds(1), BackoffStrategy.exponential(1, initial, multiplier));
        assertEquals(Duration.ofSeconds(2), BackoffStrategy.exponential(2, initial, multiplier));
        assertEquals(Duration.ofSeconds(4), BackoffStrategy.exponential(3, initial, multiplier));
        assertEquals(Duration.ofSeconds(8), BackoffStrategy.exponential(4, initial, multiplier));
    }

    @Test
    void exponentialWithDifferentMultiplier() {
        Duration initial = Duration.ofSeconds(1);
        double multiplier = 3.0;
        
        assertEquals(Duration.ofSeconds(1), BackoffStrategy.exponential(1, initial, multiplier));
        assertEquals(Duration.ofSeconds(3), BackoffStrategy.exponential(2, initial, multiplier));
        assertEquals(Duration.ofSeconds(9), BackoffStrategy.exponential(3, initial, multiplier));
    }
}