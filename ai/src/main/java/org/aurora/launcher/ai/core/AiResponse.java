package org.aurora.launcher.ai.core;

import java.time.Duration;

public class AiResponse {
    
    public enum FinishReason {
        STOP, LENGTH, ERROR
    }
    
    private final String content;
    private final int promptTokens;
    private final int completionTokens;
    private final int totalTokens;
    private final String model;
    private final Duration latency;
    private final FinishReason finishReason;
    
    public AiResponse(String content) {
        this(content, 0, 0, 0, null, null, FinishReason.STOP);
    }
    
    public AiResponse(String content, int promptTokens, int completionTokens, 
                      int totalTokens, String model, Duration latency, 
                      FinishReason finishReason) {
        this.content = content;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
        this.model = model;
        this.latency = latency;
        this.finishReason = finishReason != null ? finishReason : FinishReason.STOP;
    }
    
    public String getContent() {
        return content;
    }
    
    public int getPromptTokens() {
        return promptTokens;
    }
    
    public int getCompletionTokens() {
        return completionTokens;
    }
    
    public int getTotalTokens() {
        return totalTokens;
    }
    
    public String getModel() {
        return model;
    }
    
    public Duration getLatency() {
        return latency;
    }
    
    public FinishReason getFinishReason() {
        return finishReason;
    }
}