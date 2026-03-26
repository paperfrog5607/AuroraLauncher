package org.aurora.launcher.resource.shaderpack;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ShaderConfig {
    
    private Path configPath;
    private Properties properties;
    
    public ShaderConfig(Path configPath) {
        this.configPath = configPath;
        this.properties = new Properties();
    }
    
    public void load() throws IOException {
        if (Files.exists(configPath)) {
            try (InputStream is = Files.newInputStream(configPath)) {
                properties.load(is);
            }
        }
    }
    
    public void save() throws IOException {
        Files.createDirectories(configPath.getParent());
        try (OutputStream os = Files.newOutputStream(configPath)) {
            properties.store(os, "Shader Configuration");
        }
    }
    
    public String get(String key) {
        return properties.getProperty(key);
    }
    
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public void remove(String key) {
        properties.remove(key);
    }
    
    public Set<String> getKeys() {
        Set<String> keys = new HashSet<>();
        for (Object key : properties.keySet()) {
            keys.add((String) key);
        }
        return keys;
    }
    
    public Properties getProperties() {
        return properties;
    }
}