package org.aurora.launcher.ai.provider;

import org.aurora.launcher.ai.core.AiConfig;
import org.aurora.launcher.ai.core.AiProvider;

import java.util.*;
import java.util.function.Supplier;

public class ProviderFactory {
    
    private final Map<String, Supplier<AiProvider>> providers;
    private final Map<String, AiConfig> configs;
    
    public ProviderFactory() {
        this.providers = new LinkedHashMap<>();
        this.configs = new HashMap<>();
        registerDefaultProviders();
    }
    
    public ProviderFactory(Map<String, AiConfig> configs) {
        this();
        this.configs.putAll(configs);
    }
    
    private void registerDefaultProviders() {
        providers.put("openai", () -> new OpenAIProvider(getConfig("openai")));
        providers.put("claude", () -> new ClaudeProvider(getConfig("claude")));
        providers.put("deepseek", () -> new DeepSeekProvider(getConfig("deepseek")));
        providers.put("local", () -> new LocalProvider(getConfig("local")));
    }
    
    public AiProvider create(String name) {
        Supplier<AiProvider> supplier = providers.get(name);
        if (supplier == null) {
            supplier = providers.get("openai");
        }
        return supplier.get();
    }
    
    public List<String> getAvailableProviders() {
        return new ArrayList<>(providers.keySet());
    }
    
    public void registerProvider(String name, Supplier<AiProvider> supplier) {
        providers.put(name, supplier);
    }
    
    public void setConfig(String name, AiConfig config) {
        configs.put(name, config);
    }
    
    public AiConfig getConfig(String name) {
        return configs.getOrDefault(name, new AiConfig());
    }
    
    public boolean hasProvider(String name) {
        return providers.containsKey(name);
    }
}