package org.aurora.launcher.resource.shaderpack;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ShaderPackManager {
    
    private final Path shaderPacksDir;
    private final ShaderPackScanner scanner;
    private String currentPackId;
    private boolean shadersEnabled;
    
    public ShaderPackManager(Path shaderPacksDir) {
        this.shaderPacksDir = shaderPacksDir;
        this.scanner = new ShaderPackScanner();
        this.shadersEnabled = false;
    }
    
    public CompletableFuture<List<ShaderPack>> scan() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return scanner.scan(shaderPacksDir);
            } catch (IOException e) {
                return new ArrayList<>();
            }
        });
    }
    
    public CompletableFuture<Void> enable(String packId) {
        return CompletableFuture.runAsync(() -> {
            currentPackId = packId;
            shadersEnabled = true;
        });
    }
    
    public CompletableFuture<Void> disable() {
        return CompletableFuture.runAsync(() -> {
            shadersEnabled = false;
        });
    }
    
    public CompletableFuture<Void> delete(String packId) {
        return CompletableFuture.runAsync(() -> {
            try {
                List<ShaderPack> packs = scan().join();
                for (ShaderPack pack : packs) {
                    if (packId.equals(pack.getId())) {
                        Files.deleteIfExists(pack.getFilePath());
                        break;
                    }
                }
                
                if (packId.equals(currentPackId)) {
                    currentPackId = null;
                    shadersEnabled = false;
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete shader pack: " + packId, e);
            }
        });
    }
    
    public CompletableFuture<Void> importPack(Path source) {
        return CompletableFuture.runAsync(() -> {
            try {
                String fileName = source.getFileName().toString();
                Path target = shaderPacksDir.resolve(fileName);
                Files.createDirectories(shaderPacksDir);
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to import shader pack", e);
            }
        });
    }
    
    public Optional<ShaderPack> getCurrentPack() {
        if (!shadersEnabled || currentPackId == null) {
            return Optional.empty();
        }
        
        try {
            List<ShaderPack> packs = scan().join();
            for (ShaderPack pack : packs) {
                if (currentPackId.equals(pack.getId())) {
                    return Optional.of(pack);
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return Optional.empty();
    }
    
    public boolean isShadersEnabled() {
        return shadersEnabled;
    }
    
    public void setShadersEnabled(boolean enabled) {
        this.shadersEnabled = enabled;
    }
    
    public String getCurrentPackId() {
        return currentPackId;
    }
    
    public void setCurrentPackId(String packId) {
        this.currentPackId = packId;
    }
}