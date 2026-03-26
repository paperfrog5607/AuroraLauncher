package org.aurora.launcher.config.template;

import org.aurora.launcher.config.parser.ConfigParseException;
import org.aurora.launcher.config.parser.JsonParser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfigTemplateManager {
    
    private static ConfigTemplateManager instance;
    
    private Path templateDir;
    private Map<String, ConfigTemplate> templates;
    
    public static synchronized ConfigTemplateManager getInstance() {
        if (instance == null) {
            instance = new ConfigTemplateManager();
        }
        return instance;
    }
    
    private ConfigTemplateManager() {
        this.templates = new LinkedHashMap<>();
    }
    
    public void loadTemplates() throws IOException {
        if (templateDir == null || !Files.exists(templateDir)) {
            return;
        }
        
        templates.clear();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(templateDir, "*.json")) {
            for (Path file : stream) {
                try {
                    ConfigTemplate template = loadTemplate(file);
                    templates.put(template.getId(), template);
                } catch (Exception e) {
                    System.err.println("Failed to load template: " + file + " - " + e.getMessage());
                }
            }
        }
    }
    
    public ConfigTemplate getTemplate(String id) {
        return templates.get(id);
    }
    
    public List<ConfigTemplate> getTemplates() {
        return new ArrayList<>(templates.values());
    }
    
    public List<ConfigTemplate> getTemplatesForVersion(String gameVersion) {
        List<ConfigTemplate> result = new ArrayList<>();
        for (ConfigTemplate template : templates.values()) {
            if (gameVersion.equals(template.getGameVersion())) {
                result.add(template);
            }
        }
        return result;
    }
    
    public List<ConfigTemplate> getTemplatesForLoader(String loaderType) {
        List<ConfigTemplate> result = new ArrayList<>();
        for (ConfigTemplate template : templates.values()) {
            if (loaderType.equals(template.getLoaderType())) {
                result.add(template);
            }
        }
        return result;
    }
    
    public void saveTemplate(ConfigTemplate template) throws IOException {
        Path file = templateDir.resolve(template.getId() + ".json");
        saveTemplate(template, file);
        templates.put(template.getId(), template);
    }
    
    public void deleteTemplate(String id) throws IOException {
        Path file = templateDir.resolve(id + ".json");
        Files.deleteIfExists(file);
        templates.remove(id);
    }
    
    public void importTemplate(Path file) throws IOException, ConfigParseException {
        ConfigTemplate template = loadTemplate(file);
        templates.put(template.getId(), template);
        saveTemplate(template);
    }
    
    public void exportTemplate(String id, Path target) throws IOException {
        ConfigTemplate template = templates.get(id);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + id);
        }
        saveTemplate(template, target);
    }
    
    public void setTemplateDir(Path templateDir) {
        this.templateDir = templateDir;
    }
    
    public Path getTemplateDir() {
        return templateDir;
    }
    
    private ConfigTemplate loadTemplate(Path file) throws IOException, ConfigParseException {
        JsonParser parser = new JsonParser();
        try (InputStream input = Files.newInputStream(file)) {
            Map<String, Object> data = parser.parse(input);
            
            ConfigTemplate template = new ConfigTemplate();
            template.setId((String) data.get("id"));
            template.setName((String) data.get("name"));
            template.setDescription((String) data.get("description"));
            template.setGameVersion((String) data.get("gameVersion"));
            template.setLoaderType((String) data.get("loaderType"));
            template.setLoaderVersion((String) data.get("loaderVersion"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> defaults = (Map<String, Object>) data.get("defaults");
            if (defaults != null) {
                template.setDefaults(defaults);
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rulesData = (List<Map<String, Object>>) data.get("rules");
            if (rulesData != null) {
                for (Map<String, Object> ruleData : rulesData) {
                    TemplateRule rule = new TemplateRule();
                    rule.setKey((String) ruleData.get("key"));
                    rule.setValue(ruleData.get("value"));
                    rule.setComment((String) ruleData.get("comment"));
                    
                    String conditionStr = (String) ruleData.get("condition");
                    if (conditionStr != null) {
                        rule.setCondition(TemplateRule.RuleCondition.valueOf(conditionStr));
                    }
                    
                    template.addRule(rule);
                }
            }
            
            return template;
        }
    }
    
    private void saveTemplate(ConfigTemplate template, Path file) throws IOException {
        JsonParser parser = new JsonParser();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", template.getId());
        data.put("name", template.getName());
        data.put("description", template.getDescription());
        data.put("gameVersion", template.getGameVersion());
        data.put("loaderType", template.getLoaderType());
        data.put("loaderVersion", template.getLoaderVersion());
        data.put("defaults", template.getDefaults());
        
        List<Map<String, Object>> rulesData = new ArrayList<>();
        for (TemplateRule rule : template.getRules()) {
            Map<String, Object> ruleMap = new LinkedHashMap<>();
            ruleMap.put("key", rule.getKey());
            ruleMap.put("value", rule.getValue());
            ruleMap.put("condition", rule.getCondition().name());
            if (rule.getComment() != null) {
                ruleMap.put("comment", rule.getComment());
            }
            rulesData.add(ruleMap);
        }
        data.put("rules", rulesData);
        
        Files.createDirectories(file.getParent());
        try (OutputStream output = Files.newOutputStream(file)) {
            parser.write(output, data);
        } catch (ConfigParseException e) {
            throw new IOException("Failed to write template", e);
        }
    }
}