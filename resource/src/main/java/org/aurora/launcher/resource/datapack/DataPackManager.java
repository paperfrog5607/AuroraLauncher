package org.aurora.launcher.resource.datapack;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DataPackManager {
    
    private final Path worldDataPacksDir;
    private final Path globalDataPacksDir;
    private final DataPackScanner scanner;
    private Map<String, List<String>> worldEnabledPacks;
    
    public DataPackManager(Path worldsDir, Path globalDataPacksDir) {
        this.worldDataPacksDir = worldsDir;
        this.globalDataPacksDir = globalDataPacksDir;
        this.scanner = new DataPackScanner();
        this.worldEnabledPacks = new HashMap<>();
    }
    
    public CompletableFuture<List<DataPack>> scanWorld(String worldName) {
        return scanner.scanWorld(worldName, worldDataPacksDir);
    }
    
    public CompletableFuture<List<DataPack>> scanGlobal() {
        return scanner.scanGlobal(globalDataPacksDir);
    }
    
    public CompletableFuture<Void> enable(String packId, String worldName) {
        return CompletableFuture.runAsync(() -> {
            List<String> enabled = worldEnabledPacks.computeIfAbsent(worldName, k -> new ArrayList<>());
            if (!enabled.contains(packId)) {
                enabled.add(packId);
            }
        });
    }
    
    public CompletableFuture<Void> disable(String packId, String worldName) {
        return CompletableFuture.runAsync(() -> {
            List<String> enabled = worldEnabledPacks.get(worldName);
            if (enabled != null) {
                enabled.remove(packId);
            }
        });
    }
    
    public CompletableFuture<Void> delete(String packId) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Try to find and delete from global
                List<DataPack> globalPacks = scanGlobal().join();
                for (DataPack pack : globalPacks) {
                    if (packId.equals(pack.getId())) {
                        deletePackFile(pack);
                        break;
                    }
                }
                
                // Try to find in all worlds
                if (Files.exists(worldDataPacksDir)) {
                    try (java.util.stream.Stream<Path> worlds = Files.list(worldDataPacksDir)) {
                        worlds.filter(Files::isDirectory)
                              .forEach(worldDir -> {
                                  try {
                                      deletePackFromWorld(packId, worldDir);
                                  } catch (Exception e) {
                                      // Ignore
                                  }
                              });
                    }
                }
                
                // Remove from enabled lists
                for (List<String> enabled : worldEnabledPacks.values()) {
                    enabled.remove(packId);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete datapack: " + packId, e);
            }
        });
    }
    
    public CompletableFuture<Void> importPack(Path source, String worldName) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path targetDir = worldName != null 
                    ? worldDataPacksDir.resolve(worldName).resolve("datapacks")
                    : globalDataPacksDir;
                    
                String fileName = source.getFileName().toString();
                Path target = targetDir.resolve(fileName);
                
                Files.createDirectories(targetDir);
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to import datapack", e);
            }
        });
    }
    
    public List<DataPack> getEnabledPacks(String worldName) {
        try {
            List<DataPack> allPacks = scanWorld(worldName).join();
            List<String> enabled = worldEnabledPacks.get(worldName);
            
            if (enabled == null || enabled.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<DataPack> result = new ArrayList<>();
            for (DataPack pack : allPacks) {
                if (enabled.contains(pack.getId())) {
                    result.add(pack);
                }
            }
            
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private void deletePackFile(DataPack pack) throws IOException {
        Path path = pack.getFilePath();
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            Files.deleteIfExists(path);
        }
    }
    
    private void deletePackFromWorld(String packId, Path worldDir) throws IOException {
        Path datapacksDir = worldDir.resolve("datapacks");
        if (!Files.exists(datapacksDir)) {
            return;
        }
        
        try (java.util.stream.Stream<Path> files = Files.list(datapacksDir)) {
            files.forEach(file -> {
                String name = file.getFileName().toString();
                String id = name.replaceAll("\\.[^.]+$", "");
                if (packId.equals(id)) {
                    try {
                        deletePackFile(new DataPack(id, name) {{ setFilePath(file); }});
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            });
        }
    }
}