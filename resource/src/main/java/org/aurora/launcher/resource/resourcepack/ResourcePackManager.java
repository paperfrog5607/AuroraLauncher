package org.aurora.launcher.resource.resourcepack;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ResourcePackManager {
    
    private final Path resourcePacksDir;
    private final ResourcePackScanner scanner;
    private List<String> enabledPackIds;
    
    public ResourcePackManager(Path resourcePacksDir) {
        this.resourcePacksDir = resourcePacksDir;
        this.scanner = new ResourcePackScanner();
        this.enabledPackIds = new ArrayList<>();
    }
    
    public CompletableFuture<List<ResourcePack>> scan() {
        return scanner.scan(resourcePacksDir);
    }
    
    public CompletableFuture<Void> enable(String packId) {
        return CompletableFuture.runAsync(() -> {
            if (!enabledPackIds.contains(packId)) {
                enabledPackIds.add(packId);
            }
        });
    }
    
    public CompletableFuture<Void> disable(String packId) {
        return CompletableFuture.runAsync(() -> {
            enabledPackIds.remove(packId);
        });
    }
    
    public CompletableFuture<Void> delete(String packId) {
        return CompletableFuture.runAsync(() -> {
            try {
                List<ResourcePack> packs = scan().join();
                for (ResourcePack pack : packs) {
                    if (packId.equals(pack.getId())) {
                        Path path = pack.getFilePath();
                        if (Files.isDirectory(path)) {
                            deleteDirectory(path);
                        } else {
                            Files.deleteIfExists(path);
                        }
                        break;
                    }
                }
                enabledPackIds.remove(packId);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete pack: " + packId, e);
            }
        });
    }
    
    public CompletableFuture<Void> moveUp(String packId) {
        return CompletableFuture.runAsync(() -> {
            int index = enabledPackIds.indexOf(packId);
            if (index > 0) {
                Collections.swap(enabledPackIds, index, index - 1);
            }
        });
    }
    
    public CompletableFuture<Void> moveDown(String packId) {
        return CompletableFuture.runAsync(() -> {
            int index = enabledPackIds.indexOf(packId);
            if (index >= 0 && index < enabledPackIds.size() - 1) {
                Collections.swap(enabledPackIds, index, index + 1);
            }
        });
    }
    
    public CompletableFuture<Void> importPack(Path source) {
        return CompletableFuture.runAsync(() -> {
            try {
                String fileName = source.getFileName().toString();
                Path target = resourcePacksDir.resolve(fileName);
                Files.createDirectories(resourcePacksDir);
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to import pack", e);
            }
        });
    }
    
    public CompletableFuture<Path> exportPack(String packId, Path target) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ResourcePack> packs = scan().join();
                for (ResourcePack pack : packs) {
                    if (packId.equals(pack.getId())) {
                        Path source = pack.getFilePath();
                        Files.createDirectories(target.getParent());
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        return target;
                    }
                }
                throw new RuntimeException("Pack not found: " + packId);
            } catch (IOException e) {
                throw new RuntimeException("Failed to export pack", e);
            }
        });
    }
    
    public List<ResourcePack> getEnabledPacks() {
        try {
            List<ResourcePack> allPacks = scan().join();
            List<ResourcePack> enabled = new ArrayList<>();
            
            for (String id : enabledPackIds) {
                for (ResourcePack pack : allPacks) {
                    if (id.equals(pack.getId())) {
                        enabled.add(pack);
                        break;
                    }
                }
            }
            
            return enabled;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<ResourcePack> getAvailablePacks() {
        try {
            List<ResourcePack> allPacks = scan().join();
            List<ResourcePack> available = new ArrayList<>();
            
            for (ResourcePack pack : allPacks) {
                if (!enabledPackIds.contains(pack.getId())) {
                    available.add(pack);
                }
            }
            
            return available;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public void savePackOrder(List<String> packIds) {
        this.enabledPackIds = new ArrayList<>(packIds);
    }
    
    public List<String> getPackOrder() {
        return new ArrayList<>(enabledPackIds);
    }
    
    private void deleteDirectory(Path path) throws IOException {
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
    }
}