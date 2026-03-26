package org.aurora.launcher.ai.provider;

import org.aurora.launcher.ai.core.AiConfig;

public class DeepSeekProvider extends OpenAIProvider {
    
    private static final String DEFAULT_BASE_URL = "https://api.deepseek.com/v1/";
    private static final String DEFAULT_MODEL = "deepseek-chat";
    
    public DeepSeekProvider(AiConfig config) {
        super(config);
        if (!hasCustomBaseUrl(config)) {
            config.setBaseUrl(DEFAULT_BASE_URL);
        }
        if (config.getModel() == null) {
            config.setModel(DEFAULT_MODEL);
        }
    }
    
    @Override
    public String getName() {
        return "deepseek";
    }
    
    private boolean hasCustomBaseUrl(AiConfig config) {
        String baseUrl = config.getBaseUrl();
        return baseUrl != null && !baseUrl.contains("openai.com");
    }
}