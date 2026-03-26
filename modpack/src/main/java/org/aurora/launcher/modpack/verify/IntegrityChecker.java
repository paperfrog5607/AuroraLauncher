package org.aurora.launcher.modpack.verify;

import org.aurora.launcher.modpack.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class IntegrityChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(IntegrityChecker.class);
    
    private static final Set<String> CRITICAL_FILES = new HashSet<>(Arrays.asList(
            "options.txt",
            "servers.dat",
            "realms_persistence.json"
    ));
    
    public CompletableFuture<VerifyReport> check(Instance instance) {
        return CompletableFuture.supplyAsync(() -> {
            VerifyReport report = new VerifyReport();
            report.setInstanceId(instance.getId());
            
            Path minecraftDir = instance.getMinecraftDir();
            if (minecraftDir == null || !Files.exists(minecraftDir)) {
                report.addMissingFile(new VerifyReport.FileIssue(minecraftDir, VerifyReport.IssueType.MISSING));
                report.setPassed(false);
                return report;
            }
            
            checkCoreFiles(instance, report);
            checkModsDirectory(instance, report);
            checkConfigDirectory(instance, report);
            
            report.calculateTotals();
            
            logger.info("Integrity check completed for instance {}: {} issues found", 
                    instance.getName(), report.getTotalIssues());
            
            return report;
        });
    }
    
    private void checkCoreFiles(Instance instance, VerifyReport report) {
        Path minecraftDir = instance.getMinecraftDir();
        
        for (String criticalFile : CRITICAL_FILES) {
            Path filePath = minecraftDir.resolve(criticalFile);
            if (!Files.exists(filePath)) {
                VerifyReport.FileIssue issue = new VerifyReport.FileIssue(
                        filePath, VerifyReport.IssueType.MISSING);
                issue.setMessage("Critical file missing: " + criticalFile);
                report.addMissingFile(issue);
            }
        }
        
        Path optionsFile = minecraftDir.resolve("options.txt");
        if (Files.exists(optionsFile)) {
            try {
                List<String> lines = Files.readAllLines(optionsFile);
                if (lines.isEmpty()) {
                    VerifyReport.FileIssue issue = new VerifyReport.FileIssue(
                            optionsFile, VerifyReport.IssueType.CORRUPTED);
                    issue.setMessage("options.txt is empty");
                    report.addCorruptedFile(issue);
                }
            } catch (IOException e) {
                logger.warn("Failed to read options.txt");
            }
        }
    }
    
    private void checkModsDirectory(Instance instance, VerifyReport report) {
        Path modsDir = instance.getModsDir();
        if (modsDir == null || !Files.exists(modsDir)) {
            return;
        }
        
        try (Stream<Path> files = Files.list(modsDir)) {
            files.filter(p -> p.toString().endsWith(".jar"))
                 .forEach(modFile -> {
                     report.setFilesChecked(report.getFilesChecked() + 1);
                     checkModFile(modFile, report);
                 });
        } catch (IOException e) {
            logger.error("Failed to list mods directory", e);
        }
    }
    
    private void checkModFile(Path modFile, VerifyReport report) {
        try {
            if (Files.size(modFile) == 0) {
                VerifyReport.FileIssue issue = new VerifyReport.FileIssue(
                        modFile, VerifyReport.IssueType.CORRUPTED);
                issue.setMessage("Mod file is empty");
                report.addCorruptedFile(issue);
                return;
            }
            
            try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(modFile.toFile())) {
                boolean hasMetadata = zipFile.stream()
                        .anyMatch(entry -> entry.getName().equals("fabric.mod.json") ||
                                entry.getName().equals("META-INF/mods.toml") ||
                                entry.getName().equals("mcmod.info") ||
                                entry.getName().equals("quilt.mod.json"));
                
                if (!hasMetadata) {
                    VerifyReport.FileIssue issue = new VerifyReport.FileIssue(
                            modFile, VerifyReport.IssueType.CORRUPTED);
                    issue.setMessage("Mod file missing metadata");
                    report.addCorruptedFile(issue);
                }
            }
        } catch (IOException e) {
            VerifyReport.FileIssue issue = new VerifyReport.FileIssue(
                    modFile, VerifyReport.IssueType.CORRUPTED);
            issue.setMessage("Failed to read mod file: " + e.getMessage());
            report.addCorruptedFile(issue);
        }
    }
    
    private void checkConfigDirectory(Instance instance, VerifyReport report) {
        Path configDir = instance.getConfigDir();
        if (configDir == null || !Files.exists(configDir)) {
            return;
        }
        
        try (Stream<Path> files = Files.walk(configDir)) {
            files.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".json") || 
                             p.toString().endsWith(".toml") ||
                             p.toString().endsWith(".cfg"))
                 .forEach(configFile -> {
                     try {
                         String content = new String(Files.readAllBytes(configFile));
                         if (content.trim().isEmpty()) {
                             VerifyReport.ConfigIssue issue = new VerifyReport.ConfigIssue(
                                     configFile, "", "Config file is empty");
                             report.addConfigIssue(issue);
                         }
                     } catch (IOException e) {
                         logger.warn("Failed to read config file: {}", configFile);
                     }
                 });
        } catch (IOException e) {
            logger.error("Failed to walk config directory", e);
        }
    }
    
    public CompletableFuture<Void> repair(Instance instance, VerifyReport report) {
        return CompletableFuture.runAsync(() -> {
            for (VerifyReport.FileIssue issue : report.getCorruptedFiles()) {
                try {
                    Files.deleteIfExists(issue.getPath());
                    logger.info("Removed corrupted file: {}", issue.getPath());
                } catch (IOException e) {
                    logger.warn("Failed to remove corrupted file: {}", issue.getPath());
                }
            }
            
            Path minecraftDir = instance.getMinecraftDir();
            if (minecraftDir != null && !Files.exists(minecraftDir)) {
                try {
                    Files.createDirectories(minecraftDir);
                } catch (IOException e) {
                    logger.error("Failed to create minecraft directory", e);
                }
            }
            
            Path modsDir = instance.getModsDir();
            if (modsDir != null && !Files.exists(modsDir)) {
                try {
                    Files.createDirectories(modsDir);
                } catch (IOException e) {
                    logger.error("Failed to create mods directory", e);
                }
            }
            
            Path configDir = instance.getConfigDir();
            if (configDir != null && !Files.exists(configDir)) {
                try {
                    Files.createDirectories(configDir);
                } catch (IOException e) {
                    logger.error("Failed to create config directory", e);
                }
            }
            
            logger.info("Repair completed for instance: {}", instance.getName());
        });
    }
    
    private String calculateSHA1(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (byte b : digest.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}