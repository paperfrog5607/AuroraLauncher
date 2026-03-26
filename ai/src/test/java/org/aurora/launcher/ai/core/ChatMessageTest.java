package org.aurora.launcher.ai.core;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    @Test
    void shouldCreateUserMessage() {
        ChatMessage message = ChatMessage.user("Hello");
        
        assertEquals(ChatMessage.Role.USER, message.getRole());
        assertEquals("Hello", message.getContent());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void shouldCreateAssistantMessage() {
        ChatMessage message = ChatMessage.assistant("Hi there!");
        
        assertEquals(ChatMessage.Role.ASSISTANT, message.getRole());
        assertEquals("Hi there!", message.getContent());
    }

    @Test
    void shouldCreateSystemMessage() {
        ChatMessage message = ChatMessage.system("You are helpful");
        
        assertEquals(ChatMessage.Role.SYSTEM, message.getRole());
        assertEquals("You are helpful", message.getContent());
    }

    @Test
    void shouldCreateMessageWithTimestamp() {
        Instant time = Instant.parse("2024-01-01T00:00:00Z");
        ChatMessage message = new ChatMessage(ChatMessage.Role.USER, "Test", time);
        
        assertEquals(time, message.getTimestamp());
    }
}