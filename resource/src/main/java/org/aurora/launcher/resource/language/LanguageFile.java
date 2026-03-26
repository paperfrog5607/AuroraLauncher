package org.aurora.launcher.resource.language;

import java.nio.file.Path;
import java.util.*;

public class LanguageFile {
    
    private String languageCode;
    private Path filePath;
    private Map<String, String> entries;
    private String modId;
    
    public LanguageFile() {
        this.entries = new LinkedHashMap<>();
    }
    
    public LanguageFile(String languageCode, String modId) {
        this();
        this.languageCode = languageCode;
        this.modId = modId;
    }
    
    public String getLanguageCode() {
        return languageCode;
    }
    
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    
    public Path getFilePath() {
        return filePath;
    }
    
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
    
    public Map<String, String> getEntries() {
        return entries;
    }
    
    public void setEntries(Map<String, String> entries) {
        this.entries = entries;
    }
    
    public String getModId() {
        return modId;
    }
    
    public void setModId(String modId) {
        this.modId = modId;
    }
    
    public String get(String key) {
        return entries.get(key);
    }
    
    public void set(String key, String value) {
        entries.put(key, value);
    }
    
    public void remove(String key) {
        entries.remove(key);
    }
    
    public boolean has(String key) {
        return entries.containsKey(key);
    }
    
    public Set<String> getKeys() {
        return entries.keySet();
    }
    
    public int size() {
        return entries.size();
    }
}