package org.aurora.launcher.ai.recommendation;

import org.aurora.launcher.ai.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModRecommender {
    
    private final AiProvider provider;
    
    public ModRecommender(AiProvider provider) {
        this.provider = provider;
    }
    
    public CompletableFuture<List<RecommendationResult>> recommend(RecommendationContext context) {
        String prompt = buildPrompt(context);
        
        AiOptions options = new AiOptions();
        options.setMaxTokens(2048);
        options.setTemperature(0.8);
        
        return provider.complete(prompt, options)
                .thenApply(this::parseRecommendations);
    }
    
    private String buildPrompt(RecommendationContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Based on the following preferences, recommend 10 Minecraft mods.\n");
        sb.append("Output format: One mod per line as 'Name: ModID'\n\n");
        
        if (context.getPlayStyle() != null) {
            sb.append("Play style: ").append(context.getPlayStyle()).append("\n");
        }
        if (context.getMcVersion() != null) {
            sb.append("Minecraft version: ").append(context.getMcVersion()).append("\n");
        }
        if (context.getLoader() != null) {
            sb.append("Mod loader: ").append(context.getLoader()).append("\n");
        }
        if (!context.getExistingMods().isEmpty()) {
            sb.append("Already installed: ").append(String.join(", ", context.getExistingMods())).append("\n");
        }
        if (!context.getPreferences().isEmpty()) {
            sb.append("Preferences: ").append(String.join(", ", context.getPreferences())).append("\n");
        }
        if (!context.getAvoidCategories().isEmpty()) {
            sb.append("Avoid: ").append(String.join(", ", context.getAvoidCategories())).append("\n");
        }
        
        return sb.toString();
    }
    
    private List<RecommendationResult> parseRecommendations(AiResponse response) {
        List<RecommendationResult> results = new ArrayList<>();
        String content = response.getContent();
        
        Pattern pattern = Pattern.compile("([^:]+):\\s*(\\S+)");
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find() && results.size() < 10) {
            String name = matcher.group(1).trim();
            String modId = matcher.group(2).trim();
            
            RecommendationResult result = new RecommendationResult(name, modId);
            results.add(result);
        }
        
        return results;
    }
}