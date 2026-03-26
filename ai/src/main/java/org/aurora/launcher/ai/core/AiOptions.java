package org.aurora.launcher.ai.core;

import java.util.List;

public class AiOptions {
    
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private List<String> stopSequences;
    
    public AiOptions() {
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Integer getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public Double getTopP() {
        return topP;
    }
    
    public void setTopP(Double topP) {
        this.topP = topP;
    }
    
    public List<String> getStopSequences() {
        return stopSequences;
    }
    
    public void setStopSequences(List<String> stopSequences) {
        this.stopSequences = stopSequences;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final AiOptions options = new AiOptions();
        
        public Builder temperature(Double temperature) {
            options.setTemperature(temperature);
            return this;
        }
        
        public Builder maxTokens(Integer maxTokens) {
            options.setMaxTokens(maxTokens);
            return this;
        }
        
        public Builder topP(Double topP) {
            options.setTopP(topP);
            return this;
        }
        
        public Builder stopSequences(List<String> stopSequences) {
            options.setStopSequences(stopSequences);
            return this;
        }
        
        public AiOptions build() {
            return options;
        }
    }
}