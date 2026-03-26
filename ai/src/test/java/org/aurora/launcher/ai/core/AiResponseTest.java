package org.aurora.launcher.ai.core;

import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AiResponseTest {

    @Test
    void shouldCreateResponse() {
        AiResponse response = new AiResponse(
            "Hello!",
            10,
            5,
            15,
            "gpt-4",
            Duration.ofMillis(500),
            AiResponse.FinishReason.STOP
        );
        
        assertEquals("Hello!", response.getContent());
        assertEquals(10, response.getPromptTokens());
        assertEquals(5, response.getCompletionTokens());
        assertEquals(15, response.getTotalTokens());
        assertEquals("gpt-4", response.getModel());
        assertEquals(Duration.ofMillis(500), response.getLatency());
        assertEquals(AiResponse.FinishReason.STOP, response.getFinishReason());
    }

    @Test
    void shouldCreateSimpleResponse() {
        AiResponse response = new AiResponse("Test response");
        
        assertEquals("Test response", response.getContent());
        assertNull(response.getModel());
        assertEquals(AiResponse.FinishReason.STOP, response.getFinishReason());
    }

    @Test
    void shouldHaveFinishReasonEnum() {
        assertEquals(3, AiResponse.FinishReason.values().length);
        assertNotNull(AiResponse.FinishReason.valueOf("STOP"));
        assertNotNull(AiResponse.FinishReason.valueOf("LENGTH"));
        assertNotNull(AiResponse.FinishReason.valueOf("ERROR"));
    }
}