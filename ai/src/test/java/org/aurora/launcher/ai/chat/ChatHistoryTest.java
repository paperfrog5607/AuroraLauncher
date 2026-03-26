package org.aurora.launcher.ai.chat;

import org.aurora.launcher.ai.core.ChatMessage;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatHistoryTest {

    @Test
    void shouldCreateEmptyHistory() {
        ChatHistory history = new ChatHistory();
        
        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
    }

    @Test
    void shouldAddMessages() {
        ChatHistory history = new ChatHistory();
        
        history.add(ChatMessage.user("Hello"));
        history.add(ChatMessage.assistant("Hi!"));
        
        assertEquals(2, history.size());
        assertFalse(history.isEmpty());
    }

    @Test
    void shouldGetRecentMessages() {
        ChatHistory history = new ChatHistory();
        
        for (int i = 0; i < 5; i++) {
            history.add(ChatMessage.user("Message " + i));
        }
        
        List<ChatMessage> recent = history.getRecent(3);
        
        assertEquals(3, recent.size());
        assertEquals("Message 2", recent.get(0).getContent());
        assertEquals("Message 4", recent.get(2).getContent());
    }

    @Test
    void shouldLimitMaxMessages() {
        ChatHistory history = new ChatHistory(5);
        
        for (int i = 0; i < 10; i++) {
            history.add(ChatMessage.user("Message " + i));
        }
        
        assertEquals(5, history.size());
        List<ChatMessage> messages = history.getMessages();
        assertEquals("Message 5", messages.get(0).getContent());
    }

    @Test
    void shouldGetAllMessages() {
        ChatHistory history = new ChatHistory();
        
        history.add(ChatMessage.user("A"));
        history.add(ChatMessage.assistant("B"));
        
        List<ChatMessage> messages = history.getMessages();
        
        assertEquals(2, messages.size());
        assertEquals("A", messages.get(0).getContent());
        assertEquals("B", messages.get(1).getContent());
    }

    @Test
    void shouldClearHistory() {
        ChatHistory history = new ChatHistory();
        history.add(ChatMessage.user("Test"));
        
        history.clear();
        
        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
    }
}