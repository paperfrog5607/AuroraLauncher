package org.aurora.launcher.modpack.backup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.aurora.launcher.modpack.instance.Instance;
import org.aurora.launcher.modpack.instance.InstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupManager {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupManager.class);
    private static final String BACKUP_INFO_FILE = "backup-info.json";
    
    private final Path backupDir;
    private final InstanceManager instanceManager;
    private final BackupScheduler scheduler;
    private final Gson gson;
    
    public BackupManager(Path backupDir, InstanceManager instanceManager) {
        this.backupDir = backupDir;
        this.instanceManager = instanceManager;
        this.scheduler = new BackupScheduler(this);
        this.gson = createGson();
    }
    
    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) -> 
                        context.serialize(src.toString()))
                .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, typeOfT, context) -> 
                        Instant.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();
    }
    
    public CompletableFuture<Backup> createBackup(String instanceId, BackupOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Instance> instanceOpt = instanceManager.getInstance(instanceId);
            if (!instanceOpt.isPresent()) {
                throw new RuntimeException("Instance not found: " + instanceId);
            }
            
            Instance instance = instanceOpt.get();
            Backup backup = new Backup();
            backup.setId(UUID.randomUUID().toString());
            backup.setInstanceId(instanceId);
            backup.setType(options.getType());
            backup.setCreatedTime(Instant.now());
            
            String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                    .format(java.time.ZonedDateTime.now());
            String backupName = options.getName() != null ? options.getName() : 
                    instance.getName() + "_" + timestamp;
            backup.setName(backupName);
            backup.setDescription(options.getDescription());
            
            Path instanceBackupDir = backupDir.resolve(instanceId);
            Path backupFile = instanceBackupDir.resolve(backupName + ".zip");
            
            try {
                Files.createDirectories(instanceBackupDir);
                
                long size = createBackupZip(instance, backupFile, options);
                backup.setSize(size);
                backup.setBackupFile(backupFile);
                
                saveBackupInfo(instanceBackupDir, backup);
                
                cleanupOldBackups(instanceId, options.getMaxBackups());
                
                logger.info("Created backup: {} for instance: {}", backupName, instance.getName());
                return backup;
            } catch (IOException e) {
                throw new RuntimeException("Failed to create backup: " + e.getMessage(), e);
            }
        });
    }
    
    private long createBackupZip(Instance instance, Path backupFile, BackupOptions options) throws IOException {
        long totalSize = 0;
        Path minecraftDir = instance.getMinecraftDir();
        
        if (minecraftDir == null || !Files.exists(minecraftDir)) {
            throw new IOException("Minecraft directory not found");
        }
        
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(backupFile))) {
            zos.setLevel(java.util.zip.Deflater.DEFAULT_COMPRESSION);
            
            if (options.isIncludeMods()) {
                Path modsDir = minecraftDir.resolve("mods");
                totalSize += addDirectoryToZip(zos, modsDir, "mods");
            }
            
            if (options.isIncludeConfigs()) {
                Path configDir = minecraftDir.resolve("config");
                totalSize += addDirectoryToZip(zos, configDir, "config");
                
                Path optionsFile = minecraftDir.resolve("options.txt");
                totalSize += addFileToZip(zos, optionsFile, "options.txt");
            }
            
            if (options.isIncludeWorlds()) {
                Path savesDir = minecraftDir.resolve("saves");
                totalSize += addDirectoryToZip(zos, savesDir, "saves");
            }
            
            if (options.isIncludeLogs()) {
                Path logsDir = minecraftDir.resolve("logs");
                totalSize += addDirectoryToZip(zos, logsDir, "logs");
            }
        }
        
        return totalSize;
    }
    
    private long addDirectoryToZip(ZipOutputStream zos, Path dir, String basePath) throws IOException {
        if (!Files.exists(dir)) return 0;
        
        long totalSize = 0;
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String entryName = basePath + "/" + dir.relativize(path).toString().replace("\\", "/");
                        ZipEntry entry = new ZipEntry(entryName);
                        zos.putNextEntry(entry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        logger.warn("Failed to add file to backup: {}", path);
                    }
                });
        }
        
        return Files.size(dir);
    }
    
    private long addFileToZip(ZipOutputStream zos, Path file, String entryName) throws IOException {
        if (!Files.exists(file)) return 0;
        
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        Files.copy(file, zos);
        zos.closeEntry();
        
        return Files.size(file);
    }
    
    public CompletableFuture<Void> restore(String instanceId, String backupId) {
        return CompletableFuture.runAsync(() -> {
            Backup backup = getBackup(instanceId, backupId);
            if (backup == null) {
                throw new RuntimeException("Backup not found: " + backupId);
            }
            
            Optional<Instance> instanceOpt = instanceManager.getInstance(instanceId);
            if (!instanceOpt.isPresent()) {
                throw new RuntimeException("Instance not found: " + instanceId);
            }
            
            Instance instance = instanceOpt.get();
            Path minecraftDir = instance.getMinecraftDir();
            
            try {
                try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(backup.getBackupFile()))) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        Path targetPath = minecraftDir.resolve(entry.getName());
                        
                        if (entry.isDirectory()) {
                            Files.createDirectories(targetPath);
                        } else {
                            Files.createDirectories(targetPath.getParent());
                            Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                        zis.closeEntry();
                    }
                }
                
                logger.info("Restored backup {} for instance {}", backup.getName(), instance.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to restore backup: " + e.getMessage(), e);
            }
        });
    }
    
    public CompletableFuture<Void> deleteBackup(String backupId) {
        return CompletableFuture.runAsync(() -> {
            Backup backup = findBackupById(backupId);
            if (backup == null) {
                throw new RuntimeException("Backup not found: " + backupId);
            }
            
            try {
                Files.deleteIfExists(backup.getBackupFile());
                logger.info("Deleted backup: {}", backup.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete backup: " + e.getMessage(), e);
            }
        });
    }
    
    private Backup findBackupById(String backupId) {
        try {
            if (!Files.exists(backupDir)) return null;
            
            try (Stream<Path> dirs = Files.list(backupDir)) {
                return dirs.filter(Files::isDirectory)
                        .map(dir -> getBackup(dir.getFileName().toString(), backupId))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null);
            }
        } catch (IOException e) {
            return null;
        }
    }
    
    public List<Backup> getBackups(String instanceId) {
        List<Backup> backups = new ArrayList<>();
        Path instanceBackupDir = backupDir.resolve(instanceId);
        
        if (!Files.exists(instanceBackupDir)) {
            return backups;
        }
        
        try (Stream<Path> files = Files.list(instanceBackupDir)) {
            files.filter(p -> p.toString().endsWith(".zip"))
                 .forEach(backupFile -> {
                     Backup backup = new Backup();
                     backup.setId(extractBackupId(backupFile));
                     backup.setInstanceId(instanceId);
                     backup.setBackupFile(backupFile);
                     backup.setName(backupFile.getFileName().toString().replace(".zip", ""));
                     try {
                         backup.setSize(Files.size(backupFile));
                     } catch (IOException e) {
                         backup.setSize(0);
                     }
                     backups.add(backup);
                 });
        } catch (IOException e) {
            logger.error("Failed to list backups: {}", e.getMessage());
        }
        
        backups.sort((a, b) -> b.getCreatedTime() != null && a.getCreatedTime() != null ?
                b.getCreatedTime().compareTo(a.getCreatedTime()) : 0);
        
        return backups;
    }
    
    private String extractBackupId(Path backupFile) {
        return backupFile.getFileName().toString().hashCode() + "";
    }
    
    private Backup getBackup(String instanceId, String backupId) {
        List<Backup> backups = getBackups(instanceId);
        return backups.stream()
                .filter(b -> b.getId().equals(backupId))
                .findFirst()
                .orElse(null);
    }
    
    public Optional<Backup> getLatestBackup(String instanceId) {
        List<Backup> backups = getBackups(instanceId);
        return backups.isEmpty() ? Optional.empty() : Optional.of(backups.get(0));
    }
    
    private void saveBackupInfo(Path instanceBackupDir, Backup backup) throws IOException {
        Path infoFile = instanceBackupDir.resolve(BACKUP_INFO_FILE);
        
        List<Backup> backups = new ArrayList<>();
        if (Files.exists(infoFile)) {
            try (Reader reader = Files.newBufferedReader(infoFile)) {
                List<Backup> existing = gson.fromJson(reader, 
                        new TypeToken<List<Backup>>(){}.getType());
                if (existing != null) {
                    backups.addAll(existing);
                }
            }
        }
        
        backups.add(backup);
        
        try (Writer writer = Files.newBufferedWriter(infoFile)) {
            gson.toJson(backups, writer);
        }
    }
    
    private void cleanupOldBackups(String instanceId, int maxBackups) {
        List<Backup> backups = getBackups(instanceId);
        
        while (backups.size() > maxBackups) {
            Backup oldest = backups.remove(backups.size() - 1);
            try {
                Files.deleteIfExists(oldest.getBackupFile());
                logger.info("Deleted old backup: {}", oldest.getName());
            } catch (IOException e) {
                logger.warn("Failed to delete old backup: {}", e.getMessage());
            }
        }
    }
    
    public CompletableFuture<Void> scheduleBackup(String instanceId, BackupScheduler.ScheduleConfig config) {
        return CompletableFuture.runAsync(() -> {
            scheduler.scheduleBackup(instanceId, config);
        });
    }
    
    public void cancelSchedule(String instanceId) {
        scheduler.cancelSchedule(instanceId);
    }
    
    public void shutdown() {
        scheduler.shutdown();
    }
    
    public Path getBackupDir() {
        return backupDir;
    }
}