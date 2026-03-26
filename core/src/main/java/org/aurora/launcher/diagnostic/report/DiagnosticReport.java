package org.aurora.launcher.diagnostic.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.aurora.launcher.diagnostic.conflict.ConflictReport;
import org.aurora.launcher.diagnostic.crash.CrashReport;
import org.aurora.launcher.diagnostic.log.JavaInfo;
import org.aurora.launcher.diagnostic.log.LogAnalysisResult;
import org.aurora.launcher.diagnostic.performance.PerformanceReport;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiagnosticReport {
    private String instanceId;
    private Instant generatedTime;
    private SystemInfo systemInfo;
    private JavaInfo javaInfo;
    private MinecraftInfo minecraftInfo;
    private List<String> mods;
    private Optional<CrashReport> crashReport;
    private Optional<LogAnalysisResult> logAnalysis;
    private Optional<PerformanceReport> performance;
    private Optional<ConflictReport> conflicts;
    private List<Recommendation> recommendations;

    public DiagnosticReport() {
        this.generatedTime = Instant.now();
        this.systemInfo = new SystemInfo();
        this.mods = new ArrayList<>();
        this.recommendations = new ArrayList<>();
        this.crashReport = Optional.empty();
        this.logAnalysis = Optional.empty();
        this.performance = Optional.empty();
        this.conflicts = Optional.empty();
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Instant getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Instant generatedTime) {
        this.generatedTime = generatedTime;
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    public JavaInfo getJavaInfo() {
        return javaInfo;
    }

    public void setJavaInfo(JavaInfo javaInfo) {
        this.javaInfo = javaInfo;
    }

    public MinecraftInfo getMinecraftInfo() {
        return minecraftInfo;
    }

    public void setMinecraftInfo(MinecraftInfo minecraftInfo) {
        this.minecraftInfo = minecraftInfo;
    }

    public List<String> getMods() {
        return mods;
    }

    public void setMods(List<String> mods) {
        this.mods = mods != null ? mods : new ArrayList<>();
    }

    public void addMod(String modId) {
        if (modId != null && !mods.contains(modId)) {
            mods.add(modId);
        }
    }

    public Optional<CrashReport> getCrashReport() {
        return crashReport;
    }

    public void setCrashReport(Optional<CrashReport> crashReport) {
        this.crashReport = crashReport != null ? crashReport : Optional.empty();
    }

    public Optional<LogAnalysisResult> getLogAnalysis() {
        return logAnalysis;
    }

    public void setLogAnalysis(Optional<LogAnalysisResult> logAnalysis) {
        this.logAnalysis = logAnalysis != null ? logAnalysis : Optional.empty();
    }

    public Optional<PerformanceReport> getPerformance() {
        return performance;
    }

    public void setPerformance(Optional<PerformanceReport> performance) {
        this.performance = performance != null ? performance : Optional.empty();
    }

    public Optional<ConflictReport> getConflicts() {
        return conflicts;
    }

    public void setConflicts(Optional<ConflictReport> conflicts) {
        this.conflicts = conflicts != null ? conflicts : Optional.empty();
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
    }

    public void addRecommendation(Recommendation recommendation) {
        if (recommendation != null) {
            recommendations.add(recommendation);
        }
    }

    public String exportAsText() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Diagnostic Report ===\n");
        sb.append("Generated: ").append(generatedTime).append("\n\n");
        
        sb.append("--- System Information ---\n");
        if (systemInfo != null) {
            sb.append("OS: ").append(systemInfo.getOsName()).append(" ")
              .append(systemInfo.getOsVersion()).append(" (")
              .append(systemInfo.getOsArch()).append(")\n");
            sb.append("CPU Cores: ").append(systemInfo.getCpuCores()).append("\n");
            sb.append("Memory: ").append(systemInfo.getTotalMemoryMB()).append(" MB\n");
            if (systemInfo.getGpuName() != null) {
                sb.append("GPU: ").append(systemInfo.getGpuName());
                if (systemInfo.getGpuDriver() != null) {
                    sb.append(" (").append(systemInfo.getGpuDriver()).append(")");
                }
                sb.append("\n");
            }
        }
        
        sb.append("\n--- Java Information ---\n");
        if (javaInfo != null) {
            sb.append("Version: ").append(javaInfo.getVersion()).append("\n");
            sb.append("Vendor: ").append(javaInfo.getVendor()).append("\n");
        } else {
            sb.append("Not available\n");
        }
        
        sb.append("\n--- Minecraft ---\n");
        if (minecraftInfo != null) {
            sb.append(minecraftInfo.getSummary()).append("\n");
        }
        sb.append("Mods: ").append(mods.size()).append("\n");
        
        if (crashReport.isPresent()) {
            sb.append("\n--- Crash Analysis ---\n");
            CrashReport crash = crashReport.get();
            sb.append("Type: ").append(crash.getType()).append("\n");
            sb.append("Summary: ").append(crash.getSummary()).append("\n");
            sb.append("Confidence: ").append(crash.getConfidence()).append("\n");
        }
        
        if (conflicts.isPresent() && conflicts.get().hasConflicts()) {
            sb.append("\n--- Conflicts ---\n");
            sb.append("Found ").append(conflicts.get().getConflictCount()).append(" conflict(s)\n");
        }
        
        if (!recommendations.isEmpty()) {
            sb.append("\n--- Recommendations ---\n");
            for (Recommendation rec : recommendations) {
                sb.append("[").append(rec.getPriority()).append("] ")
                  .append(rec.getTitle()).append("\n");
                sb.append("  ").append(rec.getDescription()).append("\n");
            }
        }
        
        return sb.toString();
    }

    public String exportAsMarkdown() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# Diagnostic Report\n\n");
        sb.append("**Generated:** ").append(generatedTime).append("\n\n");
        
        sb.append("## System Information\n");
        if (systemInfo != null) {
            sb.append("| Property | Value |\n");
            sb.append("|----------|-------|\n");
            sb.append("| OS | ").append(systemInfo.getOsName()).append(" ")
              .append(systemInfo.getOsVersion()).append(" |\n");
            sb.append("| Architecture | ").append(systemInfo.getOsArch()).append(" |\n");
            sb.append("| CPU Cores | ").append(systemInfo.getCpuCores()).append(" |\n");
            sb.append("| Memory | ").append(systemInfo.getTotalMemoryMB()).append(" MB |\n");
        }
        
        sb.append("\n## Java Information\n");
        if (javaInfo != null) {
            sb.append("- Version: ").append(javaInfo.getVersion()).append("\n");
            sb.append("- Vendor: ").append(javaInfo.getVendor()).append("\n");
        }
        
        sb.append("\n## Minecraft\n");
        if (minecraftInfo != null) {
            sb.append(minecraftInfo.getSummary()).append("\n");
        }
        sb.append("- Mods installed: ").append(mods.size()).append("\n");
        
        if (crashReport.isPresent()) {
            sb.append("\n## Crash Analysis\n");
            CrashReport crash = crashReport.get();
            sb.append("**Type:** ").append(crash.getType()).append("\n\n");
            sb.append("**Summary:** ").append(crash.getSummary()).append("\n\n");
            sb.append("**Confidence:** ").append(crash.getConfidence()).append("\n");
        }
        
        if (!recommendations.isEmpty()) {
            sb.append("\n## Recommendations\n");
            for (Recommendation rec : recommendations) {
                sb.append("- **").append(rec.getTitle()).append("** (")
                  .append(rec.getPriority()).append(")\n");
                sb.append("  ").append(rec.getDescription()).append("\n");
            }
        }
        
        return sb.toString();
    }

    public byte[] exportAsJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this).getBytes();
    }
}