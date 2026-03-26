package org.aurora.launcher.ai.core;

import java.time.Duration;

public class AiConfig {
    
    private String apiKey;
    private String baseUrl;
    private String model;
    private int maxTokens = 4096;
    private double temperature = 0.7;
    private Duration timeout = Duration.ofSeconds(60);
    private int maxRetries = 3;
    
    public AiConfig() {
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public Duration getTimeout() {
        return timeout;
    }
    
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final AiConfig config = new AiConfig();
        
        public Builder apiKey(String apiKey) {
            config.setApiKey(apiKey);
            return this;
        }
        
        public Builder baseUrl(String baseUrl) {
            config.setBaseUrl(baseUrl);
            return this;
        }
        
        public Builder model(String model) {
            config.setModel(model);
            return this;
        }
        
        public Builder maxTokens(int maxTokens) {
            config.setMaxTokens(maxTokens);
            return this;
        }
        
        public Builder temperature(double temperature) {
            config.setTemperature(temperature);
            return this;
        }
        
        public Builder timeout(Duration timeout) {
            config.setTimeout(timeout);
            return this;
        }
        
        public Builder maxRetries(int maxRetries) {
            config.setMaxRetries(maxRetries);
            return this;
        }
        
        public AiConfig build() {
            return config;
        }
    }
}