package org.aurora.launcher.ai.chat;

import java.util.HashMap;
import java.util.Map;

public class SystemPrompt {
    
    private final String name;
    private final String template;
    private Map<String, String> variables;
    
    public SystemPrompt(String name, String template) {
        this.name = name;
        this.template = template;
        this.variables = new HashMap<>();
    }
    
    public String getName() {
        return name;
    }
    
    public String getTemplate() {
        return template;
    }
    
    public void setVariables(Map<String, String> variables) {
        this.variables = variables != null ? new HashMap<>(variables) : new HashMap<>();
    }
    
    public void setVariable(String key, String value) {
        variables.put(key, value);
    }
    
    public String render() {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}