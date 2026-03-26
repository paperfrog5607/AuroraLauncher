package org.aurora.launcher.ai.recommendation;

import java.util.ArrayList;
import java.util.List;

public class RecommendationContext {
    
    private String playStyle;
    private String mcVersion;
    private String loader;
    private List<String> existingMods;
    private List<String> preferences;
    private List<String> avoidCategories;
    
    public RecommendationContext() {
        this.existingMods = new ArrayList<>();
        this.preferences = new ArrayList<>();
        this.avoidCategories = new ArrayList<>();
    }
    
    public String getPlayStyle() {
        return playStyle;
    }
    
    public void setPlayStyle(String playStyle) {
        this.playStyle = playStyle;
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
    
    public List<String> getExistingMods() {
        return existingMods;
    }
    
    public void addExistingMod(String modId) {
        existingMods.add(modId);
    }
    
    public List<String> getPreferences() {
        return preferences;
    }
    
    public void addPreference(String preference) {
        preferences.add(preference);
    }
    
    public List<String> getAvoidCategories() {
        return avoidCategories;
    }
    
    public void addAvoidCategory(String category) {
        avoidCategories.add(category);
    }
}