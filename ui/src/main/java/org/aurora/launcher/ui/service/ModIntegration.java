package org.aurora.launcher.ui.service;

import java.util.List;

public interface ModIntegration {
    
    String getModId();
    
    String getModName();
    
    String getDescription();
    
    String getLoader();
    
    boolean isInstalled();
    
    List<RecipeTemplate> getRecipeTemplates();
    
    List<String> getCompletionHints();
    
    List<String> getRecipeTypes();
    
    List<String> getDatapackPaths();
}
