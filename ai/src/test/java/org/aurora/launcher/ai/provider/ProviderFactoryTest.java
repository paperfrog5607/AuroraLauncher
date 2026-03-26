package org.aurora.launcher.ai.provider;

import org.aurora.launcher.ai.core.AiConfig;
import org.aurora.launcher.ai.core.AiProvider;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProviderFactoryTest {

    @Test
    void shouldCreateDefaultProviders() {
        ProviderFactory factory = new ProviderFactory();
        
        List<String> providers = factory.getAvailableProviders();
        
        assertEquals(4, providers.size());
        assertTrue(providers.contains("openai"));
        assertTrue(providers.contains("claude"));
        assertTrue(providers.contains("deepseek"));
        assertTrue(providers.contains("local"));
    }

    @Test
    void shouldCreateOpenAIProvider() {
        ProviderFactory factory = new ProviderFactory();
        
        AiProvider provider = factory.create("openai");
        
        assertNotNull(provider);
        assertEquals("openai", provider.getName());
        assertTrue(provider instanceof OpenAIProvider);
    }

    @Test
    void shouldCreateClaudeProvider() {
        ProviderFactory factory = new ProviderFactory();
        
        AiProvider provider = factory.create("claude");
        
        assertNotNull(provider);
        assertEquals("claude", provider.getName());
        assertTrue(provider instanceof ClaudeProvider);
    }

    @Test
    void shouldCreateDeepSeekProvider() {
        ProviderFactory factory = new ProviderFactory();
        
        AiProvider provider = factory.create("deepseek");
        
        assertNotNull(provider);
        assertEquals("deepseek", provider.getName());
        assertTrue(provider instanceof DeepSeekProvider);
    }

    @Test
    void shouldCreateLocalProvider() {
        ProviderFactory factory = new ProviderFactory();
        
        AiProvider provider = factory.create("local");
        
        assertNotNull(provider);
        assertEquals("local", provider.getName());
        assertTrue(provider instanceof LocalProvider);
    }

    @Test
    void shouldReturnOpenAIForUnknownProvider() {
        ProviderFactory factory = new ProviderFactory();
        
        AiProvider provider = factory.create("unknown");
        
        assertNotNull(provider);
        assertEquals("openai", provider.getName());
    }

    @Test
    void shouldUseProvidedConfigs() {
        Map<String, AiConfig> configs = new HashMap<>();
        AiConfig openaiConfig = AiConfig.builder()
                .apiKey("test-key")
                .model("gpt-4")
                .build();
        configs.put("openai", openaiConfig);
        
        ProviderFactory factory = new ProviderFactory(configs);
        
        AiProvider provider = factory.create("openai");
        assertEquals("gpt-4", provider.getConfig().getModel());
        assertEquals("test-key", provider.getConfig().getApiKey());
    }

    @Test
    void shouldRegisterCustomProvider() {
        ProviderFactory factory = new ProviderFactory();
        
        factory.registerProvider("custom", () -> new OpenAIProvider(new AiConfig()));
        
        assertTrue(factory.hasProvider("custom"));
        AiProvider provider = factory.create("custom");
        assertNotNull(provider);
    }

    @Test
    void shouldSetConfigForProvider() {
        ProviderFactory factory = new ProviderFactory();
        
        AiConfig config = AiConfig.builder()
                .apiKey("new-key")
                .model("new-model")
                .build();
        factory.setConfig("openai", config);
        
        AiProvider provider = factory.create("openai");
        assertEquals("new-key", provider.getConfig().getApiKey());
        assertEquals("new-model", provider.getConfig().getModel());
    }
}