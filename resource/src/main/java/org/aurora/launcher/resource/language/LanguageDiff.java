package org.aurora.launcher.resource.language;

public class LanguageDiff {
    
    private String key;
    private String baseValue;
    private String otherValue;
    private DiffType type;
    
    public enum DiffType {
        ADDED, REMOVED, MODIFIED, UNCHANGED
    }
    
    public LanguageDiff() {
    }
    
    public LanguageDiff(String key, DiffType type, String baseValue, String otherValue) {
        this.key = key;
        this.type = type;
        this.baseValue = baseValue;
        this.otherValue = otherValue;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getBaseValue() {
        return baseValue;
    }
    
    public void setBaseValue(String baseValue) {
        this.baseValue = baseValue;
    }
    
    public String getOtherValue() {
        return otherValue;
    }
    
    public void setOtherValue(String otherValue) {
        this.otherValue = otherValue;
    }
    
    public DiffType getType() {
        return type;
    }
    
    public void setType(DiffType type) {
        this.type = type;
    }
}