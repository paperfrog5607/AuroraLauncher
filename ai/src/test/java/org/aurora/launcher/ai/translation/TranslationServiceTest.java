package org.aurora.launcher.ai.translation;

import org.aurora.launcher.ai.core.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class TranslationServiceTest {

    @Test
    void shouldTranslateText() throws Exception {
        TranslationService service = new TranslationService(new MockProvider());
        
        TranslationResult result = service.translate("Hello", LanguagePair.EN_TO_ZH).get();
        
        assertNotNull(result);
        assertEquals("Mock translation", result.getTranslatedText());
        assertEquals(LanguagePair.EN_TO_ZH, result.getLanguagePair());
    }
    
    @Test
    void shouldTranslateBatch() throws Exception {
        TranslationService service = new TranslationService(new MockProvider());
        
        Map<String, String> texts = new java.util.HashMap<>();
        texts.put("key1", "Hello");
        texts.put("key2", "World");
        
        Map<String, String> results = service.translateBatch(texts, LanguagePair.EN_TO_ZH).get();
        
        assertEquals(2, results.size());
        assertTrue(results.containsKey("key1"));
        assertTrue(results.containsKey("key2"));
    }
    
    private static class MockProvider extends AiProvider {
        public MockProvider() {
            super(new AiConfig());
        }
        
        @Override
        public String getName() {
            return "mock";
        }
        
        @Override
        protected okhttp3.Request buildRequest(List<ChatMessage> messages, AiOptions options) {
            return null;
        }
        
        @Override
        protected AiResponse parseResponse(String responseBody) {
            return new AiResponse("Mock translation");
        }
        
        @Override
        public CompletableFuture<AiResponse> complete(String prompt, AiOptions options) {
            return CompletableFuture.completedFuture(new AiResponse("Mock translation", 10, 5, 15, "mock", null, AiResponse.FinishReason.STOP));
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}