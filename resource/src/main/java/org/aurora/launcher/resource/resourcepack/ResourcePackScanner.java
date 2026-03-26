package org.aurora.launcher.resource.resourcepack;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcePackScanner {
    
    private final ResourcePackParser parser;
    
    public ResourcePackScanner() {
        this.parser = new ResourcePackParser();
    }
    
    public CompletableFuture<List<ResourcePack>> scan(Path dir) {
        return CompletableFuture.supplyAsync(() -> {
            List<ResourcePack> packs = new ArrayList<>();
            
            if (!Files.exists(dir)) {
                return packs;
            }
            
            try (Stream<Path> stream = Files.list(dir)) {
                List<Path> files = stream.collect(Collectors.toList());
                
                for (Path file : files) {
                    try {
                        if (shouldScan(file)) {
                            ResourcePack pack = parser.parse(file);
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
        });
    }
    
    private boolean shouldScan(Path file) {
        String fileName = file.getFileName().toString();
        return Files.isDirectory(file) || fileName.endsWith(".zip");
    }
}