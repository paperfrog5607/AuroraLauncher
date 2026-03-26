package org.aurora.launcher.ai.crash;

import org.aurora.launcher.ai.core.*;

import java.util.concurrent.CompletableFuture;

public class AiCrashAnalyzer {
    
    private final AiProvider provider;
    
    public AiCrashAnalyzer(AiProvider provider) {
        this.provider = provider;
    }
    
    public CompletableFuture<CrashAnalysisResult> analyze(String crashLog) {
        return analyze(crashLog, null);
    }
    
    public CompletableFuture<CrashAnalysisResult> analyze(String crashLog, CrashContext context) {
        String prompt = buildPrompt(crashLog, context);
        return provider.complete(prompt, createOptions())
                .thenApply(this::parseResult);
    }
    
    private String buildPrompt(String crashLog, CrashContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze the following Minecraft crash log and provide:\n");
        sb.append("1. A brief summary of the crash\n");
        sb.append("2. The type of crash (JAVA_VERSION, MEMORY, MOD_CONFLICT, CORRUPTED_FILE, CONFIGURATION, or UNKNOWN)\n");
        sb.append("3. The root cause\n");
        sb.append("4. Suspected mod IDs if applicable\n");
        sb.append("5. Suggested solutions\n");
        sb.append("6. Confidence level (0-100)\n\n");
        
        if (context != null) {
            sb.append("Context:\n");
            if (context.getMcVersion() != null) {
                sb.append("- Minecraft Version: ").append(context.getMcVersion()).append("\n");
            }
            if (context.getLoader() != null) {
                sb.append("- Mod Loader: ").append(context.getLoader()).append("\n");
            }
            if (context.getJavaVersion() != null) {
                sb.append("- Java Version: ").append(context.getJavaVersion()).append("\n");
            }
            sb.append("\n");
        }
        
        sb.append("Crash Log:\n```\n").append(crashLog).append("\n```\n");
        
        return sb.toString();
    }
    
    private AiOptions createOptions() {
        AiOptions options = new AiOptions();
        options.setMaxTokens(2048);
        options.setTemperature(0.3);
        return options;
    }
    
    private CrashAnalysisResult parseResult(AiResponse response) {
        CrashAnalysisResult result = new CrashAnalysisResult();
        String content = response.getContent();
        
        result.setSummary(extractSection(content, "Summary:", "Type:"));
        
        String typeStr = extractSection(content, "Type:", "Root Cause:");
        if (typeStr != null) {
            try {
                result.setType(CrashType.valueOf(typeStr.trim().toUpperCase().replace(" ", "_")));
            } catch (IllegalArgumentException e) {
                result.setType(CrashType.UNKNOWN);
            }
        }
        
        result.setRootCause(extractSection(content, "Root Cause:", "Suspected"));
        
        String confidenceStr = extractSection(content, "Confidence:", null);
        if (confidenceStr != null) {
            try {
                result.setConfidence(Integer.parseInt(confidenceStr.trim().replaceAll("[^0-9]", "")));
            } catch (NumberFormatException e) {
                result.setConfidence(50);
            }
        }
        
        return result;
    }
    
    private String extractSection(String content, String startMarker, String endMarker) {
        int start = content.indexOf(startMarker);
        if (start == -1) return null;
        
        start += startMarker.length();
        int end = endMarker != null ? content.indexOf(endMarker, start) : content.length();
        
        if (end == -1) end = content.length();
        
        return content.substring(start, end).trim();
    }
}