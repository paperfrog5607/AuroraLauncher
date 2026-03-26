package org.aurora.launcher.ai.core;

import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AiConfigTest {

    @Test
    void shouldCreateDefaultConfig() {
        AiConfig config = new AiConfig();
        
        assertNull(config.getApiKey());
        assertNull(config.getBaseUrl());
        assertNull(config.getModel());
        assertEquals(4096, config.getMaxTokens());
        assertEquals(0.7, config.getTemperature(), 0.001);
        assertEquals(Duration.ofSeconds(60), config.getTimeout());
        assertEquals(3, config.getMaxRetries());
    }

    @Test
    void shouldSetAndGetProperties() {
        AiConfig config = new AiConfig();
        
        config.setApiKey("test-key");
        config.setBaseUrl("https://api.example.com");
        config.setModel("gpt-4");
        config.setMaxTokens(8192);
        config.setTemperature(0.5);
        config.setTimeout(Duration.ofSeconds(120));
        config.setMaxRetries(5);
        
        assertEquals("test-key", config.getApiKey());
        assertEquals("https://api.example.com", config.getBaseUrl());
        assertEquals("gpt-4", config.getModel());
        assertEquals(8192, config.getMaxTokens());
        assertEquals(0.5, config.getTemperature(), 0.001);
        assertEquals(Duration.ofSeconds(120), config.getTimeout());
        assertEquals(5, config.getMaxRetries());
    }

    @Test
    void shouldCreateConfigWithBuilder() {
        AiConfig config = AiConfig.builder()
                .apiKey("key")
                .baseUrl("https://api.test.com")
                .model("claude-3")
                .maxTokens(2048)
                .temperature(0.8)
                .timeout(Duration.ofSeconds(30))
                .maxRetries(2)
                .build();
        
        assertEquals("key", config.getApiKey());
        assertEquals("https://api.test.com", config.getBaseUrl());
        assertEquals("claude-3", config.getModel());
        assertEquals(2048, config.getMaxTokens());
        assertEquals(0.8, config.getTemperature(), 0.001);
        assertEquals(Duration.ofSeconds(30), config.getTimeout());
        assertEquals(2, config.getMaxRetries());
    }
}