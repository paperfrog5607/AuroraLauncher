package org.aurora.launcher.diagnostic.log;

import java.util.ArrayList;
import java.util.List;

public class LogAnalysisResult {
    private List<LogEntry> errors;
    private List<LogEntry> warnings;
    private List<String> loadedMods;
    private List<String> failedMods;
    private MemoryInfo memoryInfo;
    private JavaInfo javaInfo;
    private List<String> recommendations;

    public LogAnalysisResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.loadedMods = new ArrayList<>();
        this.failedMods = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    public List<LogEntry> getErrors() {
        return errors;
    }

    public void setErrors(List<LogEntry> errors) {
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public void addError(LogEntry error) {
        if (error != null) {
            errors.add(error);
        }
    }

    public List<LogEntry> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<LogEntry> warnings) {
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public void addWarning(LogEntry warning) {
        if (warning != null) {
            warnings.add(warning);
        }
    }

    public List<String> getLoadedMods() {
        return loadedMods;
    }

    public void setLoadedMods(List<String> loadedMods) {
        this.loadedMods = loadedMods != null ? loadedMods : new ArrayList<>();
    }

    public void addLoadedMod(String modId) {
        if (modId != null && !loadedMods.contains(modId)) {
            loadedMods.add(modId);
        }
    }

    public List<String> getFailedMods() {
        return failedMods;
    }

    public void setFailedMods(List<String> failedMods) {
        this.failedMods = failedMods != null ? failedMods : new ArrayList<>();
    }

    public void addFailedMod(String modId) {
        if (modId != null && !failedMods.contains(modId)) {
            failedMods.add(modId);
        }
    }

    public MemoryInfo getMemoryInfo() {
        return memoryInfo;
    }

    public void setMemoryInfo(MemoryInfo memoryInfo) {
        this.memoryInfo = memoryInfo;
    }

    public JavaInfo getJavaInfo() {
        return javaInfo;
    }

    public void setJavaInfo(JavaInfo javaInfo) {
        this.javaInfo = javaInfo;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
    }

    public void addRecommendation(String recommendation) {
        if (recommendation != null && !recommendations.contains(recommendation)) {
            recommendations.add(recommendation);
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public int getErrorCount() {
        return errors.size();
    }

    public int getWarningCount() {
        return warnings.size();
    }
}