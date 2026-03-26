package org.aurora.launcher.config.compare;

public class ConfigDiff {
    
    private String key;
    private Object oldValue;
    private Object newValue;
    private DiffType type;
    private String oldComment;
    private String newComment;
    
    public enum DiffType {
        ADDED, REMOVED, MODIFIED
    }
    
    public ConfigDiff() {
    }
    
    public ConfigDiff(String key, DiffType type, Object oldValue, Object newValue) {
        this.key = key;
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public Object getOldValue() {
        return oldValue;
    }
    
    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }
    
    public Object getNewValue() {
        return newValue;
    }
    
    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
    
    public DiffType getType() {
        return type;
    }
    
    public void setType(DiffType type) {
        this.type = type;
    }
    
    public String getOldComment() {
        return oldComment;
    }
    
    public void setOldComment(String oldComment) {
        this.oldComment = oldComment;
    }
    
    public String getNewComment() {
        return newComment;
    }
    
    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }
}