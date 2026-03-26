package org.aurora.launcher.ai.recommendation;

public class RecommendationResult {
    
    private String name;
    private String modId;
    private String description;
    private String reason;
    private String source;
    private String url;
    private String iconUrl;
    
    public RecommendationResult() {
    }
    
    public RecommendationResult(String name, String modId) {
        this.name = name;
        this.modId = modId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getModId() {
        return modId;
    }
    
    public void setModId(String modId) {
        this.modId = modId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}