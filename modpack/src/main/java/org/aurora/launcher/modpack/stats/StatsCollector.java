package org.aurora.launcher.modpack.stats;

import org.aurora.launcher.modpack.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class StatsCollector {
    
    private static final Logger logger = LoggerFactory.getLogger(StatsCollector.class);
    
    private final Map<String, InstanceStats> cachedStats;
    
    public StatsCollector() {
        this.cachedStats = new ConcurrentHashMap<>();
    }
    
    public CompletableFuture<InstanceStats> collect(Instance instance) {
        return CompletableFuture.supplyAsync(() -> {
            InstanceStats stats = new InstanceStats();
            stats.setInstanceId(instance.getId());
            stats.setInstanceName(instance.getName());
            stats.setPlayTimeSeconds(instance.getPlayTime());
            
            Path minecraftDir = instance.getMinecraftDir();
            if (minecraftDir == null || !Files.exists(minecraftDir)) {
                return stats;
            }
            
            try {
                collectModStats(stats, instance.getModsDir());
                collectConfigStats(stats, instance.getConfigDir());
                collectWorldStats(stats, instance.getSavesDir());
                collectResourcePackStats(stats, minecraftDir.resolve("resourcepacks"));
                collectShaderPackStats(stats, minecraftDir.resolve("shaderpacks"));
                
                stats.setTotalSize(stats.getModSize() + stats.getConfigSize() + 
                        stats.getWorldSize());
                
                cachedStats.put(instance.getId(), stats);
                
            } catch (Exception e) {
                logger.error("Failed to collect stats for instance {}: {}", 
                        instance.getName(), e.getMessage());
            }
            
            return stats;
        });
    }
    
    private void collectModStats(InstanceStats stats, Path modsDir) throws IOException {
        if (modsDir == null || !Files.exists(modsDir)) {
            return;
        }
        
        long totalSize = 0;
        int count = 0;
        
        try (Stream<Path> files = Files.list(modsDir)) {
            files.filter(p -> p.toString().endsWith(".jar"))
                 .filter(p -> !p.toString().endsWith(".disabled"))
                 .forEach(p -> {
                     try {
                         long size = Files.size(p);
                         stats.addModSize(p.getFileName().toString(), size);
                     } catch (IOException e) {
                         logger.warn("Failed to get size for mod: {}", p);
                     }
                 });
        }
        
        for (Long size : stats.getModSizes().values()) {
            totalSize += size;
            count++;
        }
        
        stats.setModCount(count);
        stats.setModSize(totalSize);
    }
    
    private void collectConfigStats(InstanceStats stats, Path configDir) throws IOException {
        if (configDir == null || !Files.exists(configDir)) {
            return;
        }
        
        long totalSize = 0;
        
        try (Stream<Path> walk = Files.walk(configDir)) {
            totalSize = walk.filter(Files::isRegularFile)
                           .mapToLong(p -> {
                               try {
                                   return Files.size(p);
                               } catch (IOException e) {
                                   return 0;
                               }
                           })
                           .sum();
        }
        
        stats.setConfigSize(totalSize);
    }
    
    private void collectWorldStats(InstanceStats stats, Path savesDir) throws IOException {
        if (savesDir == null || !Files.exists(savesDir)) {
            return;
        }
        
        long totalSize = 0;
        int count = 0;
        
        try (Stream<Path> dirs = Files.list(savesDir)) {
            dirs.filter(Files::isDirectory)
                .forEach(worldDir -> {
                    try {
                        long worldSize = calculateDirectorySize(worldDir);
                        stats.setWorldSize(stats.getWorldSize() + worldSize);
                    } catch (IOException e) {
                        logger.warn("Failed to calculate world size: {}", worldDir);
                    }
                });
        }
        
        try (Stream<Path> dirs = Files.list(savesDir)) {
            count = (int) dirs.filter(Files::isDirectory).count();
        }
        
        stats.setWorldCount(count);
    }
    
    private void collectResourcePackStats(InstanceStats stats, Path resourcePacksDir) throws IOException {
        if (resourcePacksDir == null || !Files.exists(resourcePacksDir)) {
            return;
        }
        
        int count = 0;
        try (Stream<Path> files = Files.list(resourcePacksDir)) {
            count = (int) files.filter(p -> p.toString().endsWith(".zip") || Files.isDirectory(p))
                              .count();
        }
        
        stats.setResourcePackCount(count);
    }
    
    private void collectShaderPackStats(InstanceStats stats, Path shaderPacksDir) throws IOException {
        if (shaderPacksDir == null || !Files.exists(shaderPacksDir)) {
            return;
        }
        
        int count = 0;
        try (Stream<Path> files = Files.list(shaderPacksDir)) {
            count = (int) files.filter(p -> p.toString().endsWith(".zip") || Files.isDirectory(p))
                              .count();
        }
        
        stats.setShaderPackCount(count);
    }
    
    private long calculateDirectorySize(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk.filter(Files::isRegularFile)
                      .mapToLong(p -> {
                          try {
                              return Files.size(p);
                          } catch (IOException e) {
                              return 0;
                          }
                      })
                      .sum();
        }
    }
    
    public CompletableFuture<Map<String, InstanceStats>> collectAll(List<Instance> instances) {
        Map<String, InstanceStats> results = new ConcurrentHashMap<>();
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Instance instance : instances) {
            futures.add(collect(instance).thenAccept(stats -> 
                    results.put(instance.getId(), stats)));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> results);
    }
    
    public Optional<InstanceStats> getCachedStats(String instanceId) {
        return Optional.ofNullable(cachedStats.get(instanceId));
    }
    
    public void clearCache() {
        cachedStats.clear();
    }
    
    public void clearCache(String instanceId) {
        cachedStats.remove(instanceId);
    }
}