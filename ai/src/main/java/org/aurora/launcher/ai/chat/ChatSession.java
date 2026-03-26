package org.aurora.launcher.ai.chat;

import org.aurora.launcher.ai.core.ChatMessage;

import java.time.Instant;
import java.util.UUID;

public class ChatSession {
    
    private final String id;
    private SystemPrompt systemPrompt;
    private final ChatHistory history;
    private final Instant createdTime;
    private Instant lastActiveTime;
    
    public ChatSession() {
        this(UUID.randomUUID().toString());
    }
    
    public ChatSession(String id) {
        this.id = id;
        this.history = new ChatHistory();
        this.createdTime = Instant.now();
        this.lastActiveTime = this.createdTime;
    }
    
    public String getId() {
        return id;
    }
    
    public SystemPrompt getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(SystemPrompt systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public ChatHistory getHistory() {
        return history;
    }
    
    public Instant getCreatedTime() {
        return createdTime;
    }
    
    public Instant getLastActiveTime() {
        return lastActiveTime;
    }
    
    public void addMessage(ChatMessage message) {
        history.add(message);
        lastActiveTime = Instant.now();
    }
}