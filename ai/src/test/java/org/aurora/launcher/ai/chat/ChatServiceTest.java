package org.aurora.launcher.ai.chat;

import org.aurora.launcher.ai.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {

    private MockAiProvider mockProvider;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        mockProvider = new MockAiProvider();
        chatService = new ChatService(mockProvider);
    }

    @Test
    void shouldCreateSession() {
        String sessionId = chatService.createSession();
        
        assertNotNull(sessionId);
        assertTrue(chatService.hasSession(sessionId));
    }

    @Test
    void shouldSendMessage() throws Exception {
        String sessionId = chatService.createSession();
        AiResponse response = chatService.sendMessage(sessionId, "Hi").get();

        assertEquals("Mock response", response.getContent());
        assertTrue(mockProvider.chatCalled);
    }

    @Test
    void shouldMaintainHistory() throws Exception {
        String sessionId = chatService.createSession();
        chatService.sendMessage(sessionId, "Message 1").get();
        chatService.sendMessage(sessionId, "Message 2").get();

        ChatHistory history = chatService.getHistory(sessionId);
        assertEquals(4, history.size());
    }

    @Test
    void shouldClearSession() {
        String sessionId = chatService.createSession();
        
        chatService.clearSession(sessionId);
        
        assertFalse(chatService.hasSession(sessionId));
    }

    @Test
    void shouldSetSystemPrompt() {
        String sessionId = chatService.createSession();
        SystemPrompt prompt = new SystemPrompt("test", "You are helpful.");
        
        chatService.setSystemPrompt(sessionId, prompt);
        
        ChatSession session = chatService.getSession(sessionId);
        assertEquals(prompt, session.getSystemPrompt());
    }

    @Test
    void shouldCreateSessionOnDemand() throws Exception {
        AiResponse response = chatService.sendMessage("new-session", "Hi").get();
        
        assertNotNull(response);
        assertTrue(chatService.hasSession("new-session"));
    }
    
    private static class MockAiProvider extends AiProvider {
        boolean chatCalled = false;
        
        public MockAiProvider() {
            super(new AiConfig());
        }
        
        @Override
        public String getName() {
            return "mock";
        }
        
        @Override
        protected okhttp3.Request buildRequest(List<ChatMessage> messages, AiOptions options) {
            return null;
        }
        
        @Override
        protected AiResponse parseResponse(String responseBody) {
            return new AiResponse("Mock response");
        }
        
        @Override
        public CompletableFuture<AiResponse> chat(List<ChatMessage> messages, AiOptions options) {
            chatCalled = true;
            return CompletableFuture.completedFuture(new AiResponse("Mock response"));
        }
        
        @Override
        public CompletableFuture<AiResponse> complete(String prompt, AiOptions options) {
            return CompletableFuture.completedFuture(new AiResponse("Mock complete"));
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}