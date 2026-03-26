package org.aurora.launcher.config.editor;

public class ConfigEntry {
    
    private String key;
    private Object value;
    private String comment;
    private String section;
    
    public ConfigEntry() {
    }
    
    public ConfigEntry(String key, Object value) {
        this.key = key;
        this.value = value;
    }
    
    public ConfigEntry(String key, Object value, String comment) {
        this.key = key;
        this.value = value;
        this.comment = comment;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
}