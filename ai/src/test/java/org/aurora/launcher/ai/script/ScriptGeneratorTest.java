package org.aurora.launcher.ai.script;

import org.aurora.launcher.ai.core.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ScriptGeneratorTest {

    @Test
    void shouldGenerateScript() throws Exception {
        ScriptGenerator generator = new ScriptGenerator(new MockProvider());
        
        ScriptContext context = new ScriptContext();
        context.setMcVersion("1.20.1");
        
        GeneratedScript script = generator.generate(
                ScriptType.KUBEJS_RECIPE, 
                "Create a diamond from dirt recipe", 
                context
        ).get();
        
        assertNotNull(script);
        assertEquals(ScriptType.KUBEJS_RECIPE, script.getType());
        assertNotNull(script.getCode());
        assertNotNull(script.getFileName());
        assertTrue(script.getFileName().endsWith(".js"));
    }
    
    @Test
    void shouldGenerateCorrectFileExtension() throws Exception {
        ScriptGenerator generator = new ScriptGenerator(new MockProvider());
        
        GeneratedScript script = generator.generate(
                ScriptType.CRAFTTWEAKER_RECIPE,
                "Test",
                null
        ).get();
        
        assertTrue(script.getFileName().endsWith(".zs"));
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
            String mockCode = "```javascript\nconsole.log('test');\n```";
            return CompletableFuture.completedFuture(new AiResponse(mockCode));
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}