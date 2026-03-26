package org.aurora.launcher.ai.recommendation;

import org.aurora.launcher.ai.core.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ModRecommenderTest {

    @Test
    void shouldRecommendMods() throws Exception {
        ModRecommender recommender = new ModRecommender(new MockProvider());
        
        RecommendationContext context = new RecommendationContext();
        context.setPlayStyle("tech");
        context.setMcVersion("1.20.1");
        context.setLoader("forge");
        
        List<RecommendationResult> results = recommender.recommend(context).get();
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.size() <= 10);
    }
    
    @Test
    void shouldIncludeModNames() throws Exception {
        ModRecommender recommender = new ModRecommender(new MockProvider());
        
        RecommendationContext context = new RecommendationContext();
        context.setPlayStyle("magic");
        
        List<RecommendationResult> results = recommender.recommend(context).get();
        
        for (RecommendationResult result : results) {
            assertNotNull(result.getName());
            assertNotNull(result.getModId());
        }
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
            return new AiResponse("mock");
        }
        
        @Override
        public CompletableFuture<AiResponse> complete(String prompt, AiOptions options) {
            String mockResponse = "JEI: jei\n" +
                    "Create: create\n" +
                    "Mekanism: mekanism";
            return CompletableFuture.completedFuture(new AiResponse(mockResponse));
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}