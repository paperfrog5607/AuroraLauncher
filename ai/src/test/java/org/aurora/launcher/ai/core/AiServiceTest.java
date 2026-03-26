package org.aurora.launcher.ai.core;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class AiServiceTest {

    @Test
    void shouldDefineAiServiceMethods() throws Exception {
        AiService service = new TestAiService();
        
        assertEquals("test", service.getName());
        assertTrue(service.isAvailable());
        assertEquals(4096, service.getMaxTokens());
        
        AiResponse response = service.chat(Collections.emptyList(), new AiOptions()).get();
        assertEquals("test response", response.getContent());
        
        AiResponse completeResponse = service.complete("prompt", new AiOptions()).get();
        assertEquals("complete response", completeResponse.getContent());
    }
    
    private static class TestAiService implements AiService {
        @Override
        public String getName() {
            return "test";
        }
        
        @Override
        public CompletableFuture<AiResponse> chat(java.util.List<ChatMessage> messages, AiOptions options) {
            return CompletableFuture.completedFuture(new AiResponse("test response"));
        }
        
        @Override
        public CompletableFuture<AiResponse> complete(String prompt, AiOptions options) {
            return CompletableFuture.completedFuture(new AiResponse("complete response"));
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
        
        @Override
        public int getMaxTokens() {
            return 4096;
        }
    }
}