package org.aurora.launcher.ai.core;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class AiProviderTest {

    @Test
    void shouldCreateProviderWithConfig() {
        AiConfig config = AiConfig.builder()
                .apiKey("test-key")
                .model("test-model")
                .build();
        
        TestAiProvider provider = new TestAiProvider(config);
        
        assertEquals("test", provider.getName());
        assertTrue(provider.isAvailable());
        assertEquals(4096, provider.getMaxTokens());
        assertSame(config, provider.getConfig());
    }
    
    @Test
    void shouldUseConfigMaxTokens() {
        AiConfig config = AiConfig.builder()
                .maxTokens(8192)
                .build();
        
        TestAiProvider provider = new TestAiProvider(config);
        
        assertEquals(8192, provider.getMaxTokens());
    }
    
    @Test
    void shouldNotBeAvailableWithoutApiKey() {
        AiConfig config = new AiConfig();
        TestAiProvider provider = new TestAiProvider(config);
        
        assertFalse(provider.isAvailable());
    }
    
    private static class TestAiProvider extends AiProvider {
        
        public TestAiProvider(AiConfig config) {
            super(config);
        }
        
        @Override
        public String getName() {
            return "test";
        }
        
        @Override
        protected okhttp3.Request buildRequest(java.util.List<ChatMessage> messages, AiOptions options) {
            return new okhttp3.Request.Builder()
                    .url("https://api.test.com/v1/chat")
                    .build();
        }
        
        @Override
        protected AiResponse parseResponse(String responseBody) {
            return new AiResponse("test response");
        }
        
        @Override
        public CompletableFuture<AiResponse> complete(String prompt, AiOptions options) {
            return CompletableFuture.completedFuture(new AiResponse("complete response"));
        }
    }
}