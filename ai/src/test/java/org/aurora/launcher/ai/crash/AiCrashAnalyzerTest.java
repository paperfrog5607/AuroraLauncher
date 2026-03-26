package org.aurora.launcher.ai.crash;

import org.aurora.launcher.ai.core.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class AiCrashAnalyzerTest {

    @Test
    void shouldAnalyzeCrashLog() throws Exception {
        AiCrashAnalyzer analyzer = new AiCrashAnalyzer(new MockProvider());
        
        CrashAnalysisResult result = analyzer.analyze("Test crash log").get();
        
        assertNotNull(result);
        assertNotNull(result.getSummary());
    }
    
    @Test
    void shouldAnalyzeWithContext() throws Exception {
        AiCrashAnalyzer analyzer = new AiCrashAnalyzer(new MockProvider());
        
        CrashContext context = new CrashContext();
        context.setMcVersion("1.20.1");
        context.setLoader("forge");
        context.setJavaVersion("17");
        
        CrashAnalysisResult result = analyzer.analyze("Crash log", context).get();
        
        assertNotNull(result);
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
            String mockResponse = "Summary: Test crash summary\n" +
                    "Type: MOD_CONFLICT\n" +
                    "Root Cause: Test root cause\n" +
                    "Suspected Mods: mod1, mod2\n" +
                    "Solutions: Update mod1\n" +
                    "Confidence: 80";
            return CompletableFuture.completedFuture(new AiResponse(mockResponse));
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}