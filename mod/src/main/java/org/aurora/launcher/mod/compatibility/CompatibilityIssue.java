package org.aurora.launcher.mod.compatibility;

public class CompatibilityIssue {
    
    public enum IssueType {
        VERSION_MISMATCH, LOADER_MISMATCH, MISSING_DEPENDENCY, CONFLICT
    }
    
    private String modId;
    private String message;
    private IssueType type;
    
    public CompatibilityIssue(String modId, String message, IssueType type) {
        this.modId = modId;
        this.message = message;
        this.type = type;
    }
    
    public String getModId() {
        return modId;
    }
    
    public void setModId(String modId) {
        this.modId = modId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public IssueType getType() {
        return type;
    }
    
    public void setType(IssueType type) {
        this.type = type;
    }
}