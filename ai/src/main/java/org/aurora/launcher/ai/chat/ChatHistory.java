package org.aurora.launcher.ai.chat;

import org.aurora.launcher.ai.core.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatHistory {
    
    private final List<ChatMessage> messages;
    private final int maxMessages;
    
    public ChatHistory() {
        this(100);
    }
    
    public ChatHistory(int maxMessages) {
        this.messages = new ArrayList<>();
        this.maxMessages = maxMessages;
    }
    
    public void add(ChatMessage message) {
        messages.add(message);
        while (messages.size() > maxMessages) {
            messages.remove(0);
        }
    }
    
    public List<ChatMessage> getMessages() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }
    
    public List<ChatMessage> getRecent(int count) {
        int start = Math.max(0, messages.size() - count);
        return new ArrayList<>(messages.subList(start, messages.size()));
    }
    
    public int size() {
        return messages.size();
    }
    
    public boolean isEmpty() {
        return messages.isEmpty();
    }
    
    public void clear() {
        messages.clear();
    }
}