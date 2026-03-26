package org.aurora.launcher.ai.chat;

import org.aurora.launcher.ai.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {
    
    private final AiProvider provider;
    private final Map<String, ChatSession> sessions;
    
    public ChatService(AiProvider provider) {
        this.provider = provider;
        this.sessions = new ConcurrentHashMap<>();
    }
    
    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new ChatSession(sessionId));
        return sessionId;
    }
    
    public boolean hasSession(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    public ChatSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    public CompletableFuture<AiResponse> sendMessage(String sessionId, String message) {
        ChatSession session = sessions.computeIfAbsent(sessionId, ChatSession::new);
        session.addMessage(ChatMessage.user(message));
        
        List<ChatMessage> messages = buildMessages(session);
        return provider.chat(messages, new AiOptions())
                .thenApply(response -> {
                    session.addMessage(ChatMessage.assistant(response.getContent()));
                    return response;
                });
    }
    
    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }
    
    public ChatHistory getHistory(String sessionId) {
        ChatSession session = sessions.get(sessionId);
        return session != null ? session.getHistory() : null;
    }
    
    public void setSystemPrompt(String sessionId, SystemPrompt prompt) {
        ChatSession session = sessions.get(sessionId);
        if (session != null) {
            session.setSystemPrompt(prompt);
        }
    }
    
    public void setSystemPrompt(String sessionId, String promptName) {
        ChatSession session = sessions.get(sessionId);
        if (session != null && promptName != null) {
            session.setSystemPrompt(new SystemPrompt(promptName, promptName));
        }
    }
    
    private List<ChatMessage> buildMessages(ChatSession session) {
        List<ChatMessage> messages = new ArrayList<>();
        
        SystemPrompt systemPrompt = session.getSystemPrompt();
        if (systemPrompt != null) {
            messages.add(ChatMessage.system(systemPrompt.render()));
        }
        
        messages.addAll(session.getHistory().getMessages());
        
        return messages;
    }
}