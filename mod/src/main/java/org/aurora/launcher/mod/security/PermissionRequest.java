package org.aurora.launcher.mod.security;

public class PermissionRequest {
    
    public enum PermissionType {
        NETWORK_ACCESS, FILE_ACCESS, REFLECTION, CLASS_LOADER, NATIVE_CODE
    }
    
    private PermissionType type;
    private String description;
    
    public PermissionRequest(PermissionType type, String description) {
        this.type = type;
        this.description = description;
    }
    
    public PermissionType getType() {
        return type;
    }
    
    public void setType(PermissionType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}