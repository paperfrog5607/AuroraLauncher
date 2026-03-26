package org.aurora.launcher.api.mojang;

public class MojangStatus {
    
    private String service;
    private String status;
    
    public String getService() {
        return service;
    }
    
    public void setService(String service) {
        this.service = service;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isHealthy() {
        return "green".equalsIgnoreCase(status) || "healthy".equalsIgnoreCase(status);
    }
    
    public boolean isWarning() {
        return "yellow".equalsIgnoreCase(status);
    }
    
    public boolean isDown() {
        return "red".equalsIgnoreCase(status) || "unhealthy".equalsIgnoreCase(status);
    }
}