package org.aurora.launcher.ai.chat;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SystemPromptTest {

    @Test
    void shouldCreateSystemPrompt() {
        SystemPrompt prompt = new SystemPrompt("assistant", "You are a helpful assistant.");
        
        assertEquals("assistant", prompt.getName());
        assertEquals("You are a helpful assistant.", prompt.getTemplate());
    }

    @Test
    void shouldRenderTemplate() {
        SystemPrompt prompt = new SystemPrompt("translator", 
                "Translate from {{source}} to {{target}}.");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("source", "English");
        variables.put("target", "Chinese");
        prompt.setVariables(variables);
        
        assertEquals("Translate from English to Chinese.", prompt.render());
    }

    @Test
    void shouldHandleMissingVariables() {
        SystemPrompt prompt = new SystemPrompt("test", "Hello {{name}}!");
        
        assertEquals("Hello {{name}}!", prompt.render());
    }

    @Test
    void shouldRenderWithNoVariables() {
        SystemPrompt prompt = new SystemPrompt("simple", "You are helpful.");
        
        assertEquals("You are helpful.", prompt.render());
    }

    @Test
    void shouldSetVariableIndividually() {
        SystemPrompt prompt = new SystemPrompt("test", "Hello {{name}}!");
        
        prompt.setVariable("name", "World");
        
        assertEquals("Hello World!", prompt.render());
    }
}