package org.aurora.launcher.mod.scanner;

public class Dependency {
    
    public enum DependencyType {
        DEPENDS, RECOMMENDS, SUGGESTS, BREAKS, CONFLICTS
    }
    
    private String modId;
    private String versionRange;
    private DependencyType type;
    private boolean optional;
    
    public Dependency() {
        this.type = DependencyType.DEPENDS;
        this.optional = false;
    }
    
    public Dependency(String modId, DependencyType type) {
        this.modId = modId;
        this.type = type;
    }
    
    public String getModId() {
        return modId;
    }
    
    public void setModId(String modId) {
        this.modId = modId;
    }
    
    public String getVersionRange() {
        return versionRange;
    }
    
    public void setVersionRange(String versionRange) {
        this.versionRange = versionRange;
    }
    
    public DependencyType getType() {
        return type;
    }
    
    public void setType(DependencyType type) {
        this.type = type;
    }
    
    public boolean isOptional() {
        return optional;
    }
    
    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}