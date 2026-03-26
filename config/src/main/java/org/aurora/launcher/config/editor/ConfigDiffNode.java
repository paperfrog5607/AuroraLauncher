package org.aurora.launcher.config.editor;

public class ConfigDiffNode {
    
    private String key;
    private Object oldValue;
    private Object newValue;
    private DiffType diffType;
    
    public enum DiffType {
        ADDED, REMOVED, MODIFIED, UNCHANGED
    }
    
    public ConfigDiffNode() {
    }
    
    public ConfigDiffNode(String key, Object oldValue, Object newValue, DiffType diffType) {
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.diffType = diffType;
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
    
    public DiffType getDiffType() {
        return diffType;
    }
    
    public void setDiffType(DiffType diffType) {
        this.diffType = diffType;
    }
}