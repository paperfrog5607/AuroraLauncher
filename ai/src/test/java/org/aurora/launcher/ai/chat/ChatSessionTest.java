package org.aurora.launcher.ai.chat;

import org.aurora.launcher.ai.core.ChatMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ChatSessionTest {

    @Test
    void shouldCreateSession() {
        ChatSession session = new ChatSession("test-session");
        
        assertEquals("test-session", session.getId());
        assertNotNull(session.getHistory());
        assertTrue(session.getHistory().isEmpty());
        assertNotNull(session.getCreatedTime());
    }

    @Test
    void shouldAddMessage() {
        ChatSession session = new ChatSession("test");
        
        session.addMessage(ChatMessage.user("Hello"));
        
        assertEquals(1, session.getHistory().size());
        assertNotNull(session.getLastActiveTime());
    }

    @Test
    void shouldSetSystemPrompt() {
        ChatSession session = new ChatSession("test");
        SystemPrompt prompt = new SystemPrompt("assistant", "You are helpful.");
        
        session.setSystemPrompt(prompt);
        
        assertEquals(prompt, session.getSystemPrompt());
    }

    @Test
    void shouldUpdateLastActiveTime() throws InterruptedException {
        ChatSession session = new ChatSession("test");
        Instant before = session.getLastActiveTime();
        
        Thread.sleep(10);
        session.addMessage(ChatMessage.user("test"));
        
        assertTrue(session.getLastActiveTime().isAfter(before));
    }
}