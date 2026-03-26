package org.aurora.launcher.mod.security;

public class SecurityIssue {
    
    public enum IssueType {
        NETWORK_ACCESS, FILE_ACCESS, CLASS_LOADER,
        NATIVE_CODE, REFLECTION, SUSPICIOUS_PATTERN
    }
    
    private IssueType type;
    private String description;
    private RiskLevel risk;
    private String location;
    
    public SecurityIssue(IssueType type, String description, RiskLevel risk) {
        this.type = type;
        this.description = description;
        this.risk = risk;
    }
    
    public IssueType getType() {
        return type;
    }
    
    public void setType(IssueType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public RiskLevel getRisk() {
        return risk;
    }
    
    public void setRisk(RiskLevel risk) {
        this.risk = risk;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
}