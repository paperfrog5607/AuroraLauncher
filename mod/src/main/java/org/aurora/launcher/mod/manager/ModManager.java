package org.aurora.launcher.mod.manager;

import org.aurora.launcher.mod.scanner.ModInfo;
import org.aurora.launcher.mod.scanner.ModScanner;
import org.aurora.launcher.mod.scanner.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ModManager.class);
    
    private final Path modsDir;
    private final ModScanner scanner;
    private final ModEnabler enabler;
    private ScanResult lastScanResult;
    
    public ModManager(Path modsDir) {
        this.modsDir = modsDir;
        this.scanner = new ModScanner();
        this.enabler = new ModEnabler();
    }
    
    public ModManager(Path modsDir, ModScanner scanner) {
        this.modsDir = modsDir;
        this.scanner = scanner;
        this.enabler = new ModEnabler();
    }
    
    public CompletableFuture<ScanResult> refresh() {
        return scanner.scan(modsDir).thenApply(result -> {
            this.lastScanResult = result;
            return result;
        });
    }
    
    public CompletableFuture<Void> enable(String modId) {
        return CompletableFuture.runAsync(() -> {
            Optional<ModInfo> modOpt = getMod(modId);
            if (!modOpt.isPresent()) {
                throw new RuntimeException("Mod not found: " + modId);
            }
            
            ModInfo mod = modOpt.get();
            Path filePath = mod.getFilePath();
            if (filePath.getFileName().toString().endsWith(".disabled")) {
                enabler.enable(filePath);
            }
        });
    }
    
    public CompletableFuture<Void> disable(String modId) {
        return CompletableFuture.runAsync(() -> {
            Optional<ModInfo> modOpt = getMod(modId);
            if (!modOpt.isPresent()) {
                throw new RuntimeException("Mod not found: " + modId);
            }
            
            ModInfo mod = modOpt.get();
            Path filePath = mod.getFilePath();
            if (!filePath.getFileName().toString().endsWith(".disabled")) {
                enabler.disable(filePath);
            }
        });
    }
    
    public CompletableFuture<Void> delete(String modId) {
        return CompletableFuture.runAsync(() -> {
            Optional<ModInfo> modOpt = getMod(modId);
            if (!modOpt.isPresent()) {
                throw new RuntimeException("Mod not found: " + modId);
            }
            
            ModInfo mod = modOpt.get();
            try {
                Files.deleteIfExists(mod.getFilePath());
                logger.info("Deleted mod: {}", modId);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete mod: " + modId, e);
            }
        });
    }
    
    public Optional<ModInfo> getMod(String modId) {
        if (lastScanResult == null) return Optional.empty();
        
        return lastScanResult.getMods().stream()
                .filter(m -> m.getId().equals(modId))
                .findFirst();
    }
    
    public List<ModInfo> getAllMods() {
        if (lastScanResult == null) return Collections.emptyList();
        return lastScanResult.getMods();
    }
    
    public List<ModInfo> getEnabledMods() {
        if (lastScanResult == null) return Collections.emptyList();
        return lastScanResult.getMods().stream()
                .filter(m -> !m.getFilePath().getFileName().toString().endsWith(".disabled"))
                .collect(Collectors.toList());
    }
    
    public List<Path> getDisabledMods() {
        if (lastScanResult == null) return Collections.emptyList();
        return lastScanResult.getDisabledMods();
    }
    
    public Path getModsDir() {
        return modsDir;
    }
    
    public ScanResult getLastScanResult() {
        return lastScanResult;
    }
}