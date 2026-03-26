package org.aurora.launcher.mod.security;

import java.util.ArrayList;
import java.util.List;

public class SecurityReport {
    
    private String modId;
    private RiskLevel overallRisk;
    private List<SecurityIssue> issues;
    private List<PermissionRequest> permissions;
    
    public SecurityReport() {
        this.issues = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.overallRisk = RiskLevel.SAFE;
    }
    
    public String getModId() {
        return modId;
    }
    
    public void setModId(String modId) {
        this.modId = modId;
    }
    
    public RiskLevel getOverallRisk() {
        return overallRisk;
    }
    
    public void setOverallRisk(RiskLevel overallRisk) {
        this.overallRisk = overallRisk;
    }
    
    public List<SecurityIssue> getIssues() {
        return issues;
    }
    
    public void addIssue(SecurityIssue issue) {
        issues.add(issue);
    }
    
    public List<PermissionRequest> getPermissions() {
        return permissions;
    }
    
    public void addPermission(PermissionRequest permission) {
        permissions.add(permission);
    }
    
    public void calculateOverallRisk() {
        if (issues.isEmpty()) {
            overallRisk = RiskLevel.SAFE;
            return;
        }
        
        for (SecurityIssue issue : issues) {
            if (issue.getRisk().ordinal() > overallRisk.ordinal()) {
                overallRisk = issue.getRisk();
            }
        }
    }
    
    public boolean isSafe() {
        return overallRisk == RiskLevel.SAFE || overallRisk == RiskLevel.LOW;
    }
}