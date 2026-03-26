package org.aurora.launcher.config.template;

import org.aurora.launcher.config.editor.ConfigEditor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ConfigTemplate {
    
    private String id;
    private String name;
    private String description;
    private String gameVersion;
    private String loaderType;
    private String loaderVersion;
    private Map<String, Object> defaults;
    private List<TemplateRule> rules;
    
    public ConfigTemplate() {
        this.defaults = new LinkedHashMap<>();
        this.rules = new ArrayList<>();
    }
    
    public void apply(Path configPath) throws IOException {
        try {
            ConfigEditor editor = ConfigEditor.load(configPath);
            apply(editor);
            editor.save();
        } catch (Exception e) {
            throw new IOException("Failed to apply template", e);
        }
    }
    
    public void apply(ConfigEditor editor) {
        for (TemplateRule rule : rules) {
            applyRule(editor, rule);
        }
        
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!editor.has(entry.getKey())) {
                editor.set(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public void applyWithOverrides(Path configPath, Map<String, Object> overrides) throws IOException {
        try {
            ConfigEditor editor = ConfigEditor.load(configPath);
            apply(editor);
            
            for (Map.Entry<String, Object> entry : overrides.entrySet()) {
                editor.set(entry.getKey(), entry.getValue());
            }
            
            editor.save();
        } catch (Exception e) {
            throw new IOException("Failed to apply template with overrides", e);
        }
    }
    
    private void applyRule(ConfigEditor editor, TemplateRule rule) {
        switch (rule.getCondition()) {
            case ALWAYS:
                editor.set(rule.getKey(), rule.getValue(), rule.getComment());
                break;
            case IF_MISSING:
                if (!editor.has(rule.getKey())) {
                    editor.set(rule.getKey(), rule.getValue(), rule.getComment());
                }
                break;
            case IF_EMPTY:
                Object value = editor.get(rule.getKey());
                if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                    editor.set(rule.getKey(), rule.getValue(), rule.getComment());
                }
                break;
            case CUSTOM:
                break;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getGameVersion() {
        return gameVersion;
    }
    
    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }
    
    public String getLoaderType() {
        return loaderType;
    }
    
    public void setLoaderType(String loaderType) {
        this.loaderType = loaderType;
    }
    
    public String getLoaderVersion() {
        return loaderVersion;
    }
    
    public void setLoaderVersion(String loaderVersion) {
        this.loaderVersion = loaderVersion;
    }
    
    public Map<String, Object> getDefaults() {
        return defaults;
    }
    
    public void setDefaults(Map<String, Object> defaults) {
        this.defaults = defaults;
    }
    
    public List<TemplateRule> getRules() {
        return rules;
    }
    
    public void setRules(List<TemplateRule> rules) {
        this.rules = rules;
    }
    
    public void addRule(TemplateRule rule) {
        this.rules.add(rule);
    }
}