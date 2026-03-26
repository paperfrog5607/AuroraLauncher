package org.aurora.launcher.modpack.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class StatsExporter {
    
    private static final Logger logger = LoggerFactory.getLogger(StatsExporter.class);
    
    private final Gson gson;
    
    public StatsExporter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    public CompletableFuture<Path> exportToJson(InstanceStats stats, Path outputPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.createDirectories(outputPath.getParent());
                
                try (Writer writer = Files.newBufferedWriter(outputPath)) {
                    gson.toJson(stats, writer);
                }
                
                logger.info("Exported stats to: {}", outputPath);
                return outputPath;
            } catch (IOException e) {
                throw new RuntimeException("Failed to export stats: " + e.getMessage(), e);
            }
        });
    }
    
    public CompletableFuture<Path> exportToCsv(List<InstanceStats> statsList, Path outputPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.createDirectories(outputPath.getParent());
                
                try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))) {
                    writer.println("Instance ID,Instance Name,Mod Count,Resource Packs," +
                            "Shader Packs,Worlds,Total Size (MB),Play Time (hours),Launches");
                    
                    for (InstanceStats stats : statsList) {
                        writer.printf("%s,%s,%d,%d,%d,%d,%.2f,%.2f,%d%n",
                                stats.getInstanceId(),
                                stats.getInstanceName(),
                                stats.getModCount(),
                                stats.getResourcePackCount(),
                                stats.getShaderPackCount(),
                                stats.getWorldCount(),
                                stats.getTotalSize() / (1024.0 * 1024),
                                stats.getPlayTimeSeconds() / 3600.0,
                                stats.getLaunchCount());
                    }
                }
                
                logger.info("Exported stats to CSV: {}", outputPath);
                return outputPath;
            } catch (IOException e) {
                throw new RuntimeException("Failed to export stats to CSV: " + e.getMessage(), e);
            }
        });
    }
    
    public CompletableFuture<Path> exportSummary(List<InstanceStats> statsList, Path outputPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.createDirectories(outputPath.getParent());
                
                StatsSummary summary = calculateSummary(statsList);
                
                try (Writer writer = Files.newBufferedWriter(outputPath)) {
                    gson.toJson(summary, writer);
                }
                
                logger.info("Exported summary to: {}", outputPath);
                return outputPath;
            } catch (IOException e) {
                throw new RuntimeException("Failed to export summary: " + e.getMessage(), e);
            }
        });
    }
    
    private StatsSummary calculateSummary(List<InstanceStats> statsList) {
        StatsSummary summary = new StatsSummary();
        summary.totalInstances = statsList.size();
        
        long totalMods = 0;
        long totalWorlds = 0;
        long totalSize = 0;
        long totalPlayTime = 0;
        
        for (InstanceStats stats : statsList) {
            totalMods += stats.getModCount();
            totalWorlds += stats.getWorldCount();
            totalSize += stats.getTotalSize();
            totalPlayTime += stats.getPlayTimeSeconds();
        }
        
        summary.totalMods = totalMods;
        summary.totalWorlds = totalWorlds;
        summary.totalSizeBytes = totalSize;
        summary.totalPlayTimeSeconds = totalPlayTime;
        
        return summary;
    }
    
    public String formatReport(List<InstanceStats> statsList) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Instance Statistics Report ===\n\n");
        
        for (InstanceStats stats : statsList) {
            sb.append("Instance: ").append(stats.getInstanceName()).append("\n");
            sb.append("  Mods: ").append(stats.getModCount()).append("\n");
            sb.append("  Worlds: ").append(stats.getWorldCount()).append("\n");
            sb.append("  Total Size: ").append(stats.getFormattedTotalSize()).append("\n");
            sb.append("  Play Time: ").append(stats.getFormattedPlayTime()).append("\n");
            sb.append("\n");
        }
        
        StatsSummary summary = calculateSummary(statsList);
        sb.append("=== Summary ===\n");
        sb.append("Total Instances: ").append(summary.totalInstances).append("\n");
        sb.append("Total Mods: ").append(summary.totalMods).append("\n");
        sb.append("Total Worlds: ").append(summary.totalWorlds).append("\n");
        sb.append("Total Size: ").append(formatSize(summary.totalSizeBytes)).append("\n");
        sb.append("Total Play Time: ").append(formatPlayTime(summary.totalPlayTimeSeconds)).append("\n");
        
        return sb.toString();
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
    
    private String formatPlayTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return String.format("%dh %dm", hours, minutes);
    }
    
    private static class StatsSummary {
        int totalInstances;
        long totalMods;
        long totalWorlds;
        long totalSizeBytes;
        long totalPlayTimeSeconds;
    }
}