package org.aurora.launcher.config.editor;

import org.aurora.launcher.config.parser.ConfigParser;
import org.aurora.launcher.config.parser.ConfigParserFactory;
import org.aurora.launcher.config.parser.ConfigParseException;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfigEditor {
    
    protected Path configPath;
    protected ConfigParser parser;
    protected Map<String, ConfigEntry> entries;
    protected boolean modified;
    
    public static ConfigEditor load(Path configPath) throws IOException, ConfigParseException {
        ConfigParser parser = ConfigParserFactory.getParserByFile(configPath);
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported file type: " + configPath);
        }
        
        ConfigEditor editor = new ConfigEditor();
        editor.configPath = configPath;
        editor.parser = parser;
        editor.entries = new LinkedHashMap<>();
        editor.modified = false;
        
        if (Files.exists(configPath)) {
            try (InputStream input = Files.newInputStream(configPath)) {
                Map<String, Object> config = parser.parse(input);
                for (Map.Entry<String, Object> entry : config.entrySet()) {
                    editor.entries.put(entry.getKey(), new ConfigEntry(entry.getKey(), entry.getValue()));
                }
            }
        }
        
        return editor;
    }
    
    public void set(String key, Object value) {
        ConfigEntry entry = entries.get(key);
        if (entry == null) {
            entry = new ConfigEntry(key, value);
            entries.put(key, entry);
        } else {
            entry.setValue(value);
        }
        modified = true;
    }
    
    public void set(String key, Object value, String comment) {
        ConfigEntry entry = entries.get(key);
        if (entry == null) {
            entry = new ConfigEntry(key, value, comment);
            entries.put(key, entry);
        } else {
            entry.setValue(value);
            entry.setComment(comment);
        }
        modified = true;
    }
    
    public Object get(String key) {
        ConfigEntry entry = entries.get(key);
        return entry != null ? entry.getValue() : null;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        
        if (type.isInstance(value)) {
            return (T) value;
        }
        
        if (type == String.class) {
            return (T) String.valueOf(value);
        } else if (type == Integer.class) {
            if (value instanceof Number) {
                return (T) Integer.valueOf(((Number) value).intValue());
            } else if (value instanceof String) {
                try {
                    return (T) Integer.valueOf((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        } else if (type == Long.class) {
            if (value instanceof Number) {
                return (T) Long.valueOf(((Number) value).longValue());
            } else if (value instanceof String) {
                try {
                    return (T) Long.valueOf((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        } else if (type == Double.class) {
            if (value instanceof Number) {
                return (T) Double.valueOf(((Number) value).doubleValue());
            } else if (value instanceof String) {
                try {
                    return (T) Double.valueOf((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        } else if (type == Boolean.class) {
            if (value instanceof Boolean) {
                return (T) value;
            } else if (value instanceof String) {
                return (T) Boolean.valueOf((String) value);
            }
        }
        
        return null;
    }
    
    public <T> T get(String key, Class<T> type, T defaultValue) {
        T value = get(key, type);
        return value != null ? value : defaultValue;
    }
    
    public void remove(String key) {
        if (entries.remove(key) != null) {
            modified = true;
        }
    }
    
    public boolean has(String key) {
        return entries.containsKey(key);
    }
    
    public void addComment(String key, String comment) {
        ConfigEntry entry = entries.get(key);
        if (entry != null) {
            entry.setComment(comment);
            modified = true;
        }
    }
    
    public void addSection(String section) {
        ConfigEntry entry = new ConfigEntry();
        entry.setSection(section);
        entries.put(section, entry);
        modified = true;
    }
    
    public void save() throws IOException, ConfigParseException {
        saveAs(configPath);
    }
    
    public void saveAs(Path target) throws IOException, ConfigParseException {
        Map<String, Object> config = new LinkedHashMap<>();
        for (ConfigEntry entry : entries.values()) {
            if (entry.getSection() == null) {
                config.put(entry.getKey(), entry.getValue());
            }
        }
        
        Files.createDirectories(target.getParent());
        try (OutputStream output = Files.newOutputStream(target)) {
            parser.write(output, config);
        }
        
        modified = false;
    }
    
    public void reload() throws IOException, ConfigParseException {
        entries.clear();
        
        if (Files.exists(configPath)) {
            try (InputStream input = Files.newInputStream(configPath)) {
                Map<String, Object> config = parser.parse(input);
                for (Map.Entry<String, Object> entry : config.entrySet()) {
                    entries.put(entry.getKey(), new ConfigEntry(entry.getKey(), entry.getValue()));
                }
            }
        }
        
        modified = false;
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public Map<String, ConfigEntry> getEntries() {
        return Collections.unmodifiableMap(entries);
    }
    
    public List<String> getKeys() {
        return new ArrayList<>(entries.keySet());
    }
}