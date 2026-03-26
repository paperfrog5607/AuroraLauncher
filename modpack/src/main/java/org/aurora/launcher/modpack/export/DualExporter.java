package org.aurora.launcher.modpack.export;

import org.aurora.launcher.modpack.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DualExporter {
    
    private static final Logger logger = LoggerFactory.getLogger(DualExporter.class);
    
    private final CurseForgeExporter curseForgeExporter;
    private final ModrinthExporter modrinthExporter;
    
    public DualExporter() {
        this.curseForgeExporter = new CurseForgeExporter();
        this.modrinthExporter = new ModrinthExporter();
    }
    
    public CompletableFuture<Map<String, Path>> exportBoth(Instance instance) {
        return exportBoth(instance, new ExportOptions());
    }
    
    public CompletableFuture<Map<String, Path>> exportBoth(Instance instance, ExportOptions options) {
        Map<String, Path> results = new HashMap<>();
        
        CompletableFuture<Path> cfFuture = curseForgeExporter.export(instance, options)
                .thenApply(path -> {
                    results.put("curseforge", path);
                    return path;
                })
                .exceptionally(e -> {
                    logger.error("CurseForge export failed: {}", e.getMessage());
                    results.put("curseforge", null);
                    return null;
                });
        
        CompletableFuture<Path> mrFuture = modrinthExporter.export(instance, options)
                .thenApply(path -> {
                    results.put("modrinth", path);
                    return path;
                })
                .exceptionally(e -> {
                    logger.error("Modrinth export failed: {}", e.getMessage());
                    results.put("modrinth", null);
                    return null;
                });
        
        return CompletableFuture.allOf(cfFuture, mrFuture)
                .thenApply(v -> results);
    }
    
    public CompletableFuture<Map<String, Path>> exportBoth(Instance instance, ExportOptions cfOptions, ExportOptions mrOptions) {
        Map<String, Path> results = new HashMap<>();
        
        CompletableFuture<Path> cfFuture = curseForgeExporter.export(instance, cfOptions)
                .thenApply(path -> {
                    results.put("curseforge", path);
                    return path;
                })
                .exceptionally(e -> {
                    logger.error("CurseForge export failed: {}", e.getMessage());
                    results.put("curseforge", null);
                    return null;
                });
        
        CompletableFuture<Path> mrFuture = modrinthExporter.export(instance, mrOptions)
                .thenApply(path -> {
                    results.put("modrinth", path);
                    return path;
                })
                .exceptionally(e -> {
                    logger.error("Modrinth export failed: {}", e.getMessage());
                    results.put("modrinth", null);
                    return null;
                });
        
        return CompletableFuture.allOf(cfFuture, mrFuture)
                .thenApply(v -> results);
    }
    
    public CurseForgeExporter getCurseForgeExporter() {
        return curseForgeExporter;
    }
    
    public ModrinthExporter getModrinthExporter() {
        return modrinthExporter;
    }
}