package org.aurora.launcher.ai.core;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AiOptionsTest {

    @Test
    void shouldCreateDefaultOptions() {
        AiOptions options = new AiOptions();
        
        assertNull(options.getTemperature());
        assertNull(options.getMaxTokens());
        assertNull(options.getTopP());
        assertNull(options.getStopSequences());
    }

    @Test
    void shouldSetAndGetProperties() {
        AiOptions options = new AiOptions();
        List<String> stopSequences = Arrays.asList("STOP", "END");
        
        options.setTemperature(0.5);
        options.setMaxTokens(1024);
        options.setTopP(0.9);
        options.setStopSequences(stopSequences);
        
        assertEquals(0.5, options.getTemperature(), 0.001);
        assertEquals(1024, options.getMaxTokens());
        assertEquals(0.9, options.getTopP(), 0.001);
        assertEquals(stopSequences, options.getStopSequences());
    }

    @Test
    void shouldCreateOptionsWithBuilder() {
        AiOptions options = AiOptions.builder()
                .temperature(0.8)
                .maxTokens(2048)
                .topP(0.95)
                .stopSequences(Arrays.asList("STOP"))
                .build();
        
        assertEquals(0.8, options.getTemperature(), 0.001);
        assertEquals(2048, options.getMaxTokens());
        assertEquals(0.95, options.getTopP(), 0.001);
        assertEquals(1, options.getStopSequences().size());
    }
}