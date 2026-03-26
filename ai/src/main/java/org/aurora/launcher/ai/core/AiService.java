package org.aurora.launcher.ai.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AiService {
    
    String getName();
    
    CompletableFuture<AiResponse> chat(List<ChatMessage> messages, AiOptions options);
    
    CompletableFuture<AiResponse> complete(String prompt, AiOptions options);
    
    boolean isAvailable();
    
    int getMaxTokens();
}