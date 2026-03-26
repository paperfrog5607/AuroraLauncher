package org.aurora.launcher.ai.review;

import org.aurora.launcher.ai.core.*;
import org.aurora.launcher.ai.script.GeneratedScript;
import org.aurora.launcher.ai.script.ScriptType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class CodeReviewerTest {

    @Test
    void shouldReviewCode() throws Exception {
        CodeReviewer reviewer = new CodeReviewer(new MockProvider());
        
        ReviewResult result = reviewer.review("console.log('test');", "javascript").get();
        
        assertNotNull(result);
        assertTrue(result.getScore() >= 0 && result.getScore() <= 100);
    }
    
    @Test
    void shouldReviewScript() throws Exception {
        CodeReviewer reviewer = new CodeReviewer(new MockProvider());
        
        GeneratedScript script = new GeneratedScript(ScriptType.KUBEJS_EVENT, "console.log('test');");
        
        ReviewResult result = reviewer.reviewScript(script).get();
        
        assertNotNull(result);
        assertEquals(ScriptType.KUBEJS_EVENT, script.getType());
    }
    
    @Test
    void shouldParseIssues() throws Exception {
        CodeReviewer reviewer = new CodeReviewer(new MockProvider());
        
        ReviewResult result = reviewer.review("test code", "javascript").get();
        
        assertNotNull(result.getIssues());
        assertFalse(result.getIssues().isEmpty());
        
        CodeIssue issue = result.getIssues().get(0);
        assertEquals(IssueSeverity.ERROR, issue.getSeverity());
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
            String mockResponse = "Score: 85\n" +
                    "Issues: ERROR: Missing error handling\n" +
                    "Issues: WARNING: Consider using const\n" +
                    "Suggestions: - Add try-catch block\n" +
                    "Summary: Good code with minor issues";
            return CompletableFuture.completedFuture(new AiResponse(mockResponse));
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}