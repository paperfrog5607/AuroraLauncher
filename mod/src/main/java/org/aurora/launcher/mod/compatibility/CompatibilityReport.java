package org.aurora.launcher.mod.compatibility;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityReport {
    
    private String mcVersion;
    private String loader;
    private List<CompatibilityIssue> issues;
    
    public CompatibilityReport() {
        this.issues = new ArrayList<>();
    }
    
    public String getMcVersion() {
        return mcVersion;
    }
    
    public void setMcVersion(String mcVersion) {
        this.mcVersion = mcVersion;
    }
    
    public String getLoader() {
        return loader;
    }
    
    public void setLoader(String loader) {
        this.loader = loader;
    }
    
    public List<CompatibilityIssue> getIssues() {
        return issues;
    }
    
    public void addIssue(CompatibilityIssue issue) {
        issues.add(issue);
    }
    
    public boolean hasIssues() {
        return !issues.isEmpty();
    }
    
    public boolean isCompatible() {
        return issues.isEmpty();
    }
}