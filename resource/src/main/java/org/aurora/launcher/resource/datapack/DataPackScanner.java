package org.aurora.launcher.resource.datapack;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataPackScanner {
    
    private final DataPackParser parser;
    
    public DataPackScanner() {
        this.parser = new DataPackParser();
    }
    
    public CompletableFuture<List<DataPack>> scanWorld(String worldName, Path worldsDir) {
        return CompletableFuture.supplyAsync(() -> {
            Path worldDatapacksDir = worldsDir.resolve(worldName).resolve("datapacks");
            return scanDirectory(worldDatapacksDir);
        });
    }
    
    public CompletableFuture<List<DataPack>> scanGlobal(Path globalDatapacksDir) {
        return CompletableFuture.supplyAsync(() -> scanDirectory(globalDatapacksDir));
    }
    
    private List<DataPack> scanDirectory(Path dir) {
        List<DataPack> packs = new ArrayList<>();
        
        if (!Files.exists(dir)) {
            return packs;
        }
        
        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> files = stream.collect(Collectors.toList());
            
            for (Path file : files) {
                try {
                    String fileName = file.getFileName().toString();
                    if (Files.isDirectory(file) || fileName.endsWith(".zip")) {
                        DataPack pack = parser.parse(file);
                        if (pack != null) {
                            packs.add(pack);
                        }
                    }
                } catch (Exception e) {
                    // Skip invalid packs
                }
            }
        } catch (IOException e) {
            // Return what we have
        }
        
        return packs;
    }
}