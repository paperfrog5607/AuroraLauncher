package org.aurora.launcher.mod.scanner;

import org.aurora.launcher.mod.parser.ModParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModScanner {
    
    private static final Logger logger = LoggerFactory.getLogger(ModScanner.class);
    private final List<ModParser> parsers;
    
    public ModScanner() {
        this.parsers = new ArrayList<>();
    }
    
    public ModScanner(List<ModParser> parsers) {
        this.parsers = parsers != null ? new ArrayList<>(parsers) : new ArrayList<>();
    }
    
    public void addParser(ModParser parser) {
        parsers.add(parser);
    }
    
    public CompletableFuture<ScanResult> scan(Path modsDir) {
        return CompletableFuture.supplyAsync(() -> {
            ScanResult result = new ScanResult();
            result.setScanTime(Instant.now());
            
            if (!Files.exists(modsDir)) {
                try {
                    Files.createDirectories(modsDir);
                } catch (IOException e) {
                    logger.error("Failed to create mods directory: {}", modsDir, e);
                    return result;
                }
            }
            
            try (Stream<Path> files = Files.list(modsDir)) {
                List<Path> jarFiles = files
                        .filter(p -> p.getFileName().toString().endsWith(".jar") || 
                                     p.getFileName().toString().endsWith(".jar.disabled"))
                        .collect(Collectors.toList());
                
                for (Path jarFile : jarFiles) {
                    processFile(jarFile, result);
                }
            } catch (IOException e) {
                logger.error("Failed to scan mods directory: {}", modsDir, e);
                result.addError(new ScanError(modsDir, "Failed to list files", e));
            }
            
            return result;
        });
    }
    
    private void processFile(Path file, ScanResult result) {
        String fileName = file.getFileName().toString();
        
        if (fileName.endsWith(".disabled")) {
            result.addDisabledMod(file);
            return;
        }
        
        ModParser parser = getParser(file);
        if (parser == null) {
            result.addInvalidMod(file);
            return;
        }
        
        try {
            ModInfo modInfo = parser.parse(file).join();
            if (modInfo != null && modInfo.getId() != null) {
                result.addMod(modInfo);
            } else {
                result.addInvalidMod(file);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse mod: {}", file, e);
            result.addError(new ScanError(file, "Failed to parse mod", e));
            result.addInvalidMod(file);
        }
    }
    
    private ModParser getParser(Path modFile) {
        for (ModParser parser : parsers) {
            if (parser.canParse(modFile)) {
                return parser;
            }
        }
        return null;
    }
    
    public String calculateSHA1(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] fileBytes = Files.readAllBytes(file);
        byte[] hash = digest.digest(fileBytes);
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}