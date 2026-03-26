package org.aurora.launcher.ai.core;

import java.time.Instant;
import java.util.Objects;

public class ChatMessage {
    
    public enum Role {
        SYSTEM, USER, ASSISTANT
    }
    
    private final Role role;
    private final String content;
    private final Instant timestamp;
    
    public ChatMessage(Role role, String content) {
        this(role, content, Instant.now());
    }
    
    public ChatMessage(Role role, String content, Instant timestamp) {
        this.role = Objects.requireNonNull(role, "role cannot be null");
        this.content = Objects.requireNonNull(content, "content cannot be null");
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }
    
    public static ChatMessage user(String content) {
        return new ChatMessage(Role.USER, content);
    }
    
    public static ChatMessage assistant(String content) {
        return new ChatMessage(Role.ASSISTANT, content);
    }
    
    public static ChatMessage system(String content) {
        return new ChatMessage(Role.SYSTEM, content);
    }
    
    public Role getRole() {
        return role;
    }
    
    public String getContent() {
        return content;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "ChatMessage{" +
                "role=" + role +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}